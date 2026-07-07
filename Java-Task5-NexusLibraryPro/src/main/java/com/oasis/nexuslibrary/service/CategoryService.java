package com.oasis.nexuslibrary.service;

import com.oasis.nexuslibrary.dto.CategoryDto;
import java.util.List;

public interface CategoryService {
    CategoryDto save(CategoryDto categoryDto);
    CategoryDto update(Long id, CategoryDto categoryDto);
    void delete(Long id);
    CategoryDto findById(Long id);
    List<CategoryDto> findAll();
    List<CategoryDto> search(String keyword);
}
