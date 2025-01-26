package com.kairgaliyev.telegrambotdemo.command.implementation;

import com.kairgaliyev.telegrambotdemo.command.CommandHandler;
import com.kairgaliyev.telegrambotdemo.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RemoveElementCommandHandler implements CommandHandler {

    @Autowired
    private CategoryService categoryService;

    @Override
    public String getCommand() {
        return "/removeElement";
    }

    @Override
    public String getDescription() {
        return "Удалить элемент: /removeElement <имя>";
    }

    @Override
    public String handleCommand(Long chatId, String[] args) {
        if (args.length != 1) {
            return "Неверный формат команды";
        }

        try {
            if (!categoryService.existsByNameAndChatId(args[0], chatId)) {
                return "Элемент не найден";
            }
            categoryService.removeCategory(args[0], chatId);
            return "Элемент успешно удален";
        } catch (Exception e) {
            return "Ошибка удаления: " + e.getMessage();
        }
    }
}