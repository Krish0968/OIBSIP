package com.oasis.nexuslibrary.repository;

import com.oasis.nexuslibrary.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    Optional<Author> findByNameIgnoreCase(String name);
    List<Author> findByNameContainingIgnoreCase(String name);
}
