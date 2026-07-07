package com.oasis.nexuslibrary.mapper;

import com.oasis.nexuslibrary.dto.BookDto;
import com.oasis.nexuslibrary.entity.Author;
import com.oasis.nexuslibrary.entity.Book;
import com.oasis.nexuslibrary.entity.Category;

public class BookMapper {

    public static BookDto toDto(Book book) {
        if (book == null) return null;
        return new BookDto(
                book.getId(),
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor() != null ? book.getAuthor().getId() : null,
                book.getAuthor() != null ? book.getAuthor().getName() : null,
                book.getCategory() != null ? book.getCategory().getId() : null,
                book.getCategory() != null ? book.getCategory().getName() : null,
                book.getPublisher(),
                book.getLanguage(),
                book.getPublicationYear(),
                book.getTotalCopies(),
                book.getAvailableCopies(),
                book.getShelfNumber(),
                book.getStatus(),
                book.getDescription()
        );
    }

    public static Book toEntity(BookDto dto, Author author, Category category) {
        if (dto == null) return null;
        return new Book(
                dto.getId(),
                dto.getIsbn(),
                dto.getTitle(),
                author,
                category,
                dto.getPublisher(),
                dto.getLanguage(),
                dto.getPublicationYear(),
                dto.getTotalCopies(),
                dto.getAvailableCopies() != null ? dto.getAvailableCopies() : dto.getTotalCopies(),
                dto.getShelfNumber(),
                dto.getStatus(),
                dto.getDescription()
        );
    }
}
