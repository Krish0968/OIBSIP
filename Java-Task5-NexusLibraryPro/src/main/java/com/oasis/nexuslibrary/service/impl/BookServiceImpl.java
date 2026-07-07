package com.oasis.nexuslibrary.service.impl;

import com.oasis.nexuslibrary.dto.BookDto;
import com.oasis.nexuslibrary.entity.Author;
import com.oasis.nexuslibrary.entity.Book;
import com.oasis.nexuslibrary.entity.BookStatus;
import com.oasis.nexuslibrary.entity.Category;
import com.oasis.nexuslibrary.exception.BadRequestException;
import com.oasis.nexuslibrary.exception.ResourceNotFoundException;
import com.oasis.nexuslibrary.mapper.BookMapper;
import com.oasis.nexuslibrary.repository.AuthorRepository;
import com.oasis.nexuslibrary.repository.BookRepository;
import com.oasis.nexuslibrary.repository.CategoryRepository;
import com.oasis.nexuslibrary.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;

    public BookServiceImpl(BookRepository bookRepository, AuthorRepository authorRepository, CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public BookDto save(BookDto bookDto) {
        if (bookRepository.existsByIsbn(bookDto.getIsbn())) {
            throw new BadRequestException("Book with ISBN '" + bookDto.getIsbn() + "' already exists.");
        }

        Author author = authorRepository.findById(bookDto.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + bookDto.getAuthorId()));

        Category category = categoryRepository.findById(bookDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + bookDto.getCategoryId()));

        Book book = BookMapper.toEntity(bookDto, author, category);
        
        // Auto-configure initial availability copies matching total copies
        book.setAvailableCopies(book.getTotalCopies());
        book.updateStatusBasedOnAvailability();

        Book savedBook = bookRepository.save(book);
        return BookMapper.toDto(savedBook);
    }

    @Override
    public BookDto update(Long id, BookDto bookDto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        bookRepository.findByIsbn(bookDto.getIsbn())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new BadRequestException("Another book with ISBN '" + bookDto.getIsbn() + "' already exists.");
                    }
                });

        Author author = authorRepository.findById(bookDto.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + bookDto.getAuthorId()));

        Category category = categoryRepository.findById(bookDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + bookDto.getCategoryId()));

        // Calculate new available copies if total copies changed
        int diff = bookDto.getTotalCopies() - book.getTotalCopies();
        int newAvailable = book.getAvailableCopies() + diff;
        if (newAvailable < 0) {
            throw new BadRequestException("Cannot reduce total copies below active checkouts. Current available: " + book.getAvailableCopies());
        }

        book.setIsbn(bookDto.getIsbn());
        book.setTitle(bookDto.getTitle());
        book.setAuthor(author);
        book.setCategory(category);
        book.setPublisher(bookDto.getPublisher());
        book.setLanguage(bookDto.getLanguage());
        book.setPublicationYear(bookDto.getPublicationYear());
        book.setTotalCopies(bookDto.getTotalCopies());
        book.setAvailableCopies(newAvailable);
        book.setShelfNumber(bookDto.getShelfNumber());
        book.setDescription(bookDto.getDescription());
        book.updateStatusBasedOnAvailability();

        Book updatedBook = bookRepository.save(book);
        return BookMapper.toDto(updatedBook);
    }

    @Override
    public void delete(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        
        // We can only delete a book if it is not currently issued to anyone
        if (book.getAvailableCopies() < book.getTotalCopies()) {
            throw new BadRequestException("Cannot delete book as some copies are currently issued.");
        }
        
        bookRepository.delete(book);
    }

    @Override
    @Transactional(readOnly = true)
    public BookDto findById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        return BookMapper.toDto(book);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookDto> findFiltered(String search, Long categoryId, Long authorId, BookStatus status, Pageable pageable) {
        return bookRepository.findBooksFiltered(search, categoryId, authorId, status, pageable)
                .map(BookMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public long countTotalBooks() {
        return bookRepository.count();
    }
}
