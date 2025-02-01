package com.kairgaliyev.telegrambotdemo.command.implementation;

import com.kairgaliyev.telegrambotdemo.command.CommandHandler;
import com.kairgaliyev.telegrambotdemo.command.DocumentHandler;
import com.kairgaliyev.telegrambotdemo.service.implementation.CategoryExcelService;
import com.kairgaliyev.telegrambotdemo.service.implementation.CategoryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * Класс с названием командой и описанием для импорта эксел файла пользователи
 */
@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class UploadCommandHandler implements CommandHandler, DocumentHandler {
    private static final Logger logger = LoggerFactory.getLogger(UploadCommandHandler.class);
    private final CategoryExcelService categoryExcelService;

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

    @Override
    public String handleDocument(Long chatId, InputStream inputStream) {
        try {
            categoryExcelService.importFromExcel(inputStream, chatId);
            logger.info("Данные импортированный в чат с идентификатором: {}", chatId);
            return "Данные успешно импортированы";
        } catch (Exception e) {
            logger.error("Ошибка во время импорт файла, ошибка: {}", e.getMessage());
            return "Ошибка во время импорта импорта";
        }
    }
}