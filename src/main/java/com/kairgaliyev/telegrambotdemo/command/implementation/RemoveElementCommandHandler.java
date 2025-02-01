package com.kairgaliyev.telegrambotdemo.command.implementation;

import com.kairgaliyev.telegrambotdemo.command.CommandHandler;
import com.kairgaliyev.telegrambotdemo.service.implementation.CategoryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Класс с названием командой и описанием для удаления категории
 */
@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class RemoveElementCommandHandler implements CommandHandler {
    private final CategoryService categoryService;
    private static final Logger logger = LoggerFactory.getLogger(RemoveElementCommandHandler.class);


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
            logger.error("Ошибка во время удаление элемента, ошибка: {}", e.getMessage());
            return "Ошибка удаления: " + e.getMessage();
        }
    }
}