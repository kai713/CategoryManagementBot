package com.kairgaliyev.telegrambotdemo.command.implementation;

import com.kairgaliyev.telegrambotdemo.command.CommandHandler;
import com.kairgaliyev.telegrambotdemo.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class DownloadCommandHandler implements CommandHandler {
    @Autowired
    private CategoryService categoryService;

    @Override
    public String getCommand() {
        return "/download";
    }

    @Override
    public String getDescription() {
        return "Скачать дерево в Excel (Файл не должен превышать 20мб)";
    }

    @Override
    public String handleCommand(Long chatId, String[] args) {
        try {
            byte[] excelData = categoryService.exportToExcel(chatId);
            return "FILE:" + Base64.getEncoder().encodeToString(excelData) + ":categories.xlsx";
        } catch (Exception e) {
            return "Ошибка при генерации файла: " + e.getMessage();
        }
    }
}