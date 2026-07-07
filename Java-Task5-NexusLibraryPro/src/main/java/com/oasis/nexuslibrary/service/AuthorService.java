package com.oasis.nexuslibrary.service;

import com.oasis.nexuslibrary.dto.AuthorDto;
import java.util.List;

public interface AuthorService {
    AuthorDto save(AuthorDto authorDto);
    AuthorDto update(Long id, AuthorDto authorDto);
    void delete(Long id);
    AuthorDto findById(Long id);
    List<AuthorDto> findAll();
    List<AuthorDto> search(String keyword);
}
