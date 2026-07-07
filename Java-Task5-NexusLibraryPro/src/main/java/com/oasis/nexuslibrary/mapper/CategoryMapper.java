package com.oasis.nexuslibrary.mapper;

import com.oasis.nexuslibrary.dto.CategoryDto;
import com.oasis.nexuslibrary.entity.Category;

public class CategoryMapper {

    public static CategoryDto toDto(Category category) {
        if (category == null) return null;
        return new CategoryDto(category.getId(), category.getName(), category.getDescription());
    }

    public static Category toEntity(CategoryDto dto) {
        if (dto == null) return null;
        return new Category(dto.getId(), dto.getName(), dto.getDescription());
    }
}
