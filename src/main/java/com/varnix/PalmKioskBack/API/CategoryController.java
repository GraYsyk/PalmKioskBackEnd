package com.varnix.PalmKioskBack.API;

import com.varnix.PalmKioskBack.Dtos.CategoryDTO;
import com.varnix.PalmKioskBack.Entities.Category;
import com.varnix.PalmKioskBack.Exceptions.AppError;
import com.varnix.PalmKioskBack.Services.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cat")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/create/{name}")
    public ResponseEntity<?> create(@PathVariable String name) {
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new AppError(HttpStatus.BAD_REQUEST.value(), "Category name cannot be empty"));
        }

        if (categoryService.findByName(name).isPresent()) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Category with this name already exist"), HttpStatus.BAD_REQUEST);
        }

        Category category = new Category();
        category.setName(name);
        categoryService.save(category);

        return ResponseEntity.ok(new CategoryDTO(category.getId(), category.getName()));
    }

    @GetMapping("/all")
    public ResponseEntity<List<CategoryDTO>> getAll() {
        List<Category> categories = categoryService.findAll();
        List<CategoryDTO> dtoList = categories.stream()
                .map(cat -> new CategoryDTO(cat.getId(), cat.getName(), null)) // передаем null для items, можно потом расширить
                .toList();
        return ResponseEntity.ok(dtoList);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestParam String name) {
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new AppError(HttpStatus.BAD_REQUEST.value(), "Category name cannot be empty"));
        }

        var optionalCategory = categoryService.findById(id);
        if (optionalCategory.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new AppError(HttpStatus.NOT_FOUND.value(), "Category not found"));
        }

        if (categoryService.findByName(name).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(new AppError(HttpStatus.BAD_REQUEST.value(), "Category with this name already exist"));
        }

        Category category = optionalCategory.get();
        category.setName(name.trim());
        categoryService.save(category);

        return ResponseEntity.ok(new CategoryDTO(category.getId(), category.getName(), null));
    }

    // Удалить категорию по id
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        var optionalCategory = categoryService.findById(id);
        if (optionalCategory.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new AppError(HttpStatus.NOT_FOUND.value(), "Category not found"));
        }

        categoryService.deleteById(id);
        return ResponseEntity.ok().body("Category deleted successfully");
    }
}
