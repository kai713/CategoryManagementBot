package com.kairgaliyev.telegrambotdemo.service;

import com.kairgaliyev.telegrambotdemo.entity.Category;
import com.kairgaliyev.telegrambotdemo.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional
    public Category addCategory(String name, String parentName) {
        Optional<Category> parentCategory = categoryRepository.findByName(parentName);

        if (parentName != null && parentCategory.isEmpty()) {
            throw new RuntimeException("Родительская категория '" + parentName + "' не найдена!");
        }

        Category category = new Category(name, parentCategory.orElse(null));
        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Transactional
    public void removeCategory(String name) {
        Optional<Category> category = categoryRepository.findByName(name);

        if (category.isPresent()) {
            categoryRepository.delete(category.get());
        } else {
            throw new RuntimeException("Категория '" + name + "' не найдена!");
        }
    }

    public Optional<Category> findCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

}

