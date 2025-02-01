package com.kairgaliyev.telegrambotdemo.service;

import com.kairgaliyev.telegrambotdemo.entity.Category;
import com.kairgaliyev.telegrambotdemo.exception.CategoryException;
import com.kairgaliyev.telegrambotdemo.exception.CategoryNotFoundException;

public interface ICategoryService {
    Category addRootCategory(String name, Long chatId) throws CategoryNotFoundException;

    Category addChildCategory(String parentName, String childName, Long chatId) throws CategoryException;

    void removeCategory(String name, Long chatId) throws CategoryNotFoundException;

    String viewTree(Long chatId);
}
