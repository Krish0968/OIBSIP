package com.oasis.nexuslibrary.service.impl;

import com.oasis.nexuslibrary.dto.AuthorDto;
import com.oasis.nexuslibrary.entity.Author;
import com.oasis.nexuslibrary.exception.BadRequestException;
import com.oasis.nexuslibrary.exception.ResourceNotFoundException;
import com.oasis.nexuslibrary.mapper.AuthorMapper;
import com.oasis.nexuslibrary.repository.AuthorRepository;
import com.oasis.nexuslibrary.service.AuthorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public AuthorDto save(AuthorDto authorDto) {
        if (authorRepository.findByNameIgnoreCase(authorDto.getName()).isPresent()) {
            throw new BadRequestException("Author with name '" + authorDto.getName() + "' already exists.");
        }
        Author author = AuthorMapper.toEntity(authorDto);
        Author savedAuthor = authorRepository.save(author);
        return AuthorMapper.toDto(savedAuthor);
    }

    @Override
    public AuthorDto update(Long id, AuthorDto authorDto) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + id));

        authorRepository.findByNameIgnoreCase(authorDto.getName())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new BadRequestException("Another author with name '" + authorDto.getName() + "' already exists.");
                    }
                });

        author.setName(authorDto.getName());
        author.setBiography(authorDto.getBiography());
        Author updatedAuthor = authorRepository.save(author);
        return AuthorMapper.toDto(updatedAuthor);
    }

    @Override
    public void delete(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + id));
        if (author.getBooks() != null && !author.getBooks().isEmpty()) {
            throw new BadRequestException("Cannot delete author as they have books associated with them.");
        }
        authorRepository.delete(author);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthorDto findById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + id));
        return AuthorMapper.toDto(author);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthorDto> findAll() {
        return authorRepository.findAll().stream()
                .map(AuthorMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthorDto> search(String keyword) {
        return authorRepository.findByNameContainingIgnoreCase(keyword).stream()
                .map(AuthorMapper::toDto)
                .toList();
    }
}
