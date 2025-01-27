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

    /**
     * Метод для отображения всех категории с соответствующей иерархий
     *
     * @param chatId идентификатор чата
     * @param args   аргументы
     * @return String
     */
    @Override
    public String handleCommand(Long chatId, String[] args) {
        String tree = categoryService.buildTree(chatId);
        return tree.isEmpty() ? "Дерево пустое" : tree;
    }
}