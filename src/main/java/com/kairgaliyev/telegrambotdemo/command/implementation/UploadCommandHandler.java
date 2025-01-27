package com.kairgaliyev.telegrambotdemo.command.implementation;

import com.kairgaliyev.telegrambotdemo.command.CommandHandler;
import com.kairgaliyev.telegrambotdemo.command.DocumentHandler;
import com.kairgaliyev.telegrambotdemo.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * Класс с названием командой и описанием для импорта эксел файла пользователи
 */
@Component
public class UploadCommandHandler implements CommandHandler, DocumentHandler {
    @Autowired
    private CategoryService categoryService;
    private static final Logger logger = LoggerFactory.getLogger(UploadCommandHandler.class);

    @Override
    public String getCommand() {
        return "/upload";
    }

    @Override
    public String getDescription() {
        return "Загрузить дерево из Excel (Файл не должен превышать 20мб)";
    }

    @Override
    public String handleCommand(Long chatId, String[] args) {
        return "Отправьте файл формата xlsx";
    }

    /**
     * Метод для импорта эксел файла и последующей обработки объектов category
     *
     * @param chatId      идентификатор чата
     * @param inputStream аргумент
     * @return String
     */

    //TODO show correct form of excel img
    @Override
    public String handleDocument(Long chatId, InputStream inputStream) {
        try {
            categoryService.importFromExcel(inputStream, chatId);
            logger.info("Данные импортированный в чат с идентификатором: {}", chatId);
            return "Данные успешно импортированы";
        } catch (Exception e) {
            logger.error("Ошибка во время импорт файла, ошибка: {}", e.getMessage());
            return "Ошибка во время импорта импорта";
        }
    }
}