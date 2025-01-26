package com.kairgaliyev.telegrambotdemo.command.implementation;

import com.kairgaliyev.telegrambotdemo.command.CommandHandler;
import com.kairgaliyev.telegrambotdemo.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Класс с названием командой и описанием для удаления категории
 */
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

    /**
     * Метод для удаления категории за счет вызова метода removeCategory В категории сервисе
     *
     * @param chatId идентификатор чата
     * @param args   аргументы команды
     * @return String
     */
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