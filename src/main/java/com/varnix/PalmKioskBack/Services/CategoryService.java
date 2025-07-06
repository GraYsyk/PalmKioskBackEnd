package com.varnix.PalmKioskBack.Services;

import com.varnix.PalmKioskBack.Entities.Category;
import com.varnix.PalmKioskBack.Repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    @Transactional
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    @ReadOnlyProperty
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @ReadOnlyProperty
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    @Transactional
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
}
