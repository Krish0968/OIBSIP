package com.oasis.nexuslibrary.mapper;

import com.oasis.nexuslibrary.dto.AuthorDto;
import com.oasis.nexuslibrary.entity.Author;

public class AuthorMapper {

    public static AuthorDto toDto(Author author) {
        if (author == null) return null;
        return new AuthorDto(author.getId(), author.getName(), author.getBiography());
    }

    public static Author toEntity(AuthorDto dto) {
        if (dto == null) return null;
        return new Author(dto.getId(), dto.getName(), dto.getBiography());
    }
}
