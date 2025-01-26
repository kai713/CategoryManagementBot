package com.kairgaliyev.telegrambotdemo.command.implementation;

import com.kairgaliyev.telegrambotdemo.command.CommandHandler;
import com.kairgaliyev.telegrambotdemo.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ViewTreeCommandHandler implements CommandHandler {
    @Autowired
    private CategoryService categoryService;

    @Override
    public String getCommand() {
        return "/viewTree";
    }

    @Override
    public String getDescription() {
        return "Показать дерево категорий";
    }

    @Override
    public String handleCommand(Long chatId, String[] args) {
        try {
            String tree = categoryService.buildTree(chatId);
            return tree.isEmpty() ? "Дерево пустое" : tree;
        } catch (Exception e) {
            return "Ошибка при построении дерева: " + e.getMessage();
        }
    }
}