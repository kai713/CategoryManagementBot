package com.kairgaliyev.telegrambotdemo.command;

import com.kairgaliyev.telegrambotdemo.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommandHandler {

    @Autowired
    private CategoryService categoryService;

    public String handleCommand(String command) {
        String[] parts = command.split(" ", 3);
        String action = parts[0];

        switch (action) {
            case "/addElement":
                return handleAddElement(parts);
            case "/removeElement":
                return handleRemoveElement(parts);
            case "/viewTree":
                return handleViewTree();
            case "/help":
                return handleHelp();
            default:
                return "Неизвестная команда. Используйте /help для списка команд.";
        }
    }

    private String handleAddElement(String[] parts) {
        if (parts.length < 2) {
            return "Ошибка: укажите название элемента (/addElement <название>).";
        }
        String name = parts[1];
        String parentName = parts.length == 3 ? parts[2] : null;

        try {
            categoryService.addCategory(name, parentName);
            return "Категория '" + name + "' успешно добавлена!";
        } catch (RuntimeException e) {
            return e.getMessage();
        }
    }

    private String handleRemoveElement(String[] parts) {
        if (parts.length < 2) {
            return "Ошибка: укажите название элемента (/removeElement <название>).";
        }
        String name = parts[1];

        try {
            categoryService.removeCategory(name);
            return "Категория '" + name + "' удалена.";
        } catch (RuntimeException e) {
            return e.getMessage();
        }
    }

    private String handleViewTree() {
        List<String> categories = categoryService.getAllCategories()
                .stream()
                .map(cat -> cat.getParent() == null ? cat.getName() : "  ├─ " + cat.getName() + " (род. " + cat.getParent().getName() + ")")
                .toList();

        return categories.isEmpty() ? "Дерево категорий пусто." : String.join("\n", categories);
    }

    private String handleHelp() {
        return """
                Доступные команды:
                /addElement <название> - Добавить корневую категорию
                /addElement <родитель> <название> - Добавить подкатегорию
                /removeElement <название> - Удалить категорию
                /viewTree - Просмотреть дерево категорий
                /help - Список команд
                """;
    }
}

