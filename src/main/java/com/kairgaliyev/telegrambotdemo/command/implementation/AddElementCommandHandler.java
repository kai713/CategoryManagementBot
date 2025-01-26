package com.kairgaliyev.telegrambotdemo.command.implementation;

import com.kairgaliyev.telegrambotdemo.command.CommandHandler;
import com.kairgaliyev.telegrambotdemo.entity.Category;
import com.kairgaliyev.telegrambotdemo.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Component
public class AddElementCommandHandler implements CommandHandler {
    @Autowired
    private CategoryService categoryService;

    @Override
    public String getCommand() {
        return "/addElement";
    }

    @Override
    public String getDescription() {
        return "Добавить элемент: /addElement [родитель] <имя> \n Добавить элемент: /addElement <имя>";
    }

    @Override
    public String handleCommand(Long chatId, String[] args) {
        try {
            if (args.length < 1 || args.length > 2) {
                return "Неверный формат команды. Используйте: /addElement [родитель] <имя>";
            }

            for (String arg : args) {
                if (arg.isBlank() || arg.length() > 50) {
                    return "Некорректное имя элемента: " + arg;
                }
            }
            if (args.length == 1) {
                Category category = categoryService.addRootCategory(args[0], chatId);
                return "Добавлен корневой элемент: " + category.getName();
            } else if (args.length == 2) {
                Category category = categoryService.addChildCategory(args[0], args[1], chatId);
                return "Добавлен дочерний элемент: " + category.getName();
            } else {
                throw new IllegalArgumentException("Неверное количество аргументов");
            }
        } catch (Exception e) {
            return "Ошибка: " + e.getMessage();
        }
    }
}