package com.kairgaliyev.telegrambotdemo.command.implementation;

import com.kairgaliyev.telegrambotdemo.command.CommandHandler;
import com.kairgaliyev.telegrambotdemo.command.DocumentHandler;
import com.kairgaliyev.telegrambotdemo.service.CategoryService;
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
            categoryService.importFromExcel(inputStream, chatId);
            return "Данные успешно импортированы";
        } catch (Exception e) {
            return "Ошибка импорта: " + e.getMessage();
        }
    }
}