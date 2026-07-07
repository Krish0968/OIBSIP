package com.oasis.nexuslibrary.repository;

import com.oasis.nexuslibrary.entity.Book;
import com.oasis.nexuslibrary.entity.BookStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    Optional<Book> findByIsbn(String isbn);
    
    boolean existsByIsbn(String isbn);
    
    long countByStatus(BookStatus status);

    @Query("SELECT b FROM Book b WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           " LOWER(b.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(b.isbn) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(b.author.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(b.publisher) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:categoryId IS NULL OR b.category.id = :categoryId) AND " +
           "(:authorId IS NULL OR b.author.id = :authorId) AND " +
           "(:status IS NULL OR b.status = :status)")
    Page<Book> findBooksFiltered(@Param("search") String search, 
                                 @Param("categoryId") Long categoryId, 
                                 @Param("authorId") Long authorId, 
                                 @Param("status") BookStatus status, 
                                 Pageable pageable);
}
