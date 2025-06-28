package com.varnix.PalmKioskBack.Services;

import com.varnix.PalmKioskBack.Entities.Category;
import com.varnix.PalmKioskBack.Repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Optional<Category> findByName(String name) {
        return categoryRepository.findCategoryByName(name);
    }
}
