package com.kairgaliyev.telegrambotdemo.command.implementation;

import com.kairgaliyev.telegrambotdemo.command.CommandHandler;
import com.kairgaliyev.telegrambotdemo.entity.Category;
import com.kairgaliyev.telegrambotdemo.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Класс с названием командой и описанием для добавления категории
 */
@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class AddElementCommandHandler implements CommandHandler {
    private final CategoryService categoryService;
    private static final Logger logger = LoggerFactory.getLogger(AddElementCommandHandler.class);


    @Override
    public String getCommand() {
        return "/addElement";
    }

    @Override
    public String getDescription() {
        return "Добавить элемент: /addElement [родитель] <имя> \n Добавить элемент: /addElement <имя>";
    }

    /**
     * Метод для добавления категории с обработкой аргументов
     *
     * @param chatId идентификатор чата
     * @param args   аргументы команды [родитель] <имя> /addElement <имя>
     * @return
     */
    @Override
    public String handleCommand(Long chatId, String[] args) {
        logger.info("Вызван метод в классе AddElementCommandHandler, с идентификатором чата: {}", chatId);
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

                //TODO fix and refactor
            } else if (args.length == 2) {
                Category category = categoryService.addChildCategory(args[0], args[1], chatId);
                return "Добавлен дочерний элемент: " + category.getName();
            } else {
                throw new IllegalArgumentException("Неверное количество аргументов");
            }
        } catch (Exception e) {
            logger.error("Ошибка во время добавления элемента {}", e.getMessage());
            return "Ошибка: " + e.getMessage();
        }
    }
}