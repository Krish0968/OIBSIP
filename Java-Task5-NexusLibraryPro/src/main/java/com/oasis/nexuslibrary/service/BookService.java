package com.oasis.nexuslibrary.service;

import com.oasis.nexuslibrary.dto.BookDto;
import com.oasis.nexuslibrary.entity.BookStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(BookDto bookDto);
    BookDto update(Long id, BookDto bookDto);
    void delete(Long id);
    BookDto findById(Long id);
    Page<BookDto> findFiltered(String search, Long categoryId, Long authorId, BookStatus status, Pageable pageable);
    long countTotalBooks();
}
