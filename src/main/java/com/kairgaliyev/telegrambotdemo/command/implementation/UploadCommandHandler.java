package com.kairgaliyev.telegrambotdemo.command.implementation;

import com.kairgaliyev.telegrambotdemo.command.CommandHandler;
import com.kairgaliyev.telegrambotdemo.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class UploadCommandHandler implements CommandHandler {
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

    public String handleDocument(Long chatId, InputStream inputStream) {
        try {
            categoryService.importFromExcel(inputStream, chatId);
            return "Данные успешно импортированы";
        } catch (Exception e) {
            return "Ошибка импорта: " + e.getMessage();
        }
    }
}