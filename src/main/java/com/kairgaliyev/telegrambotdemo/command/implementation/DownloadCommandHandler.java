package com.kairgaliyev.telegrambotdemo.command.implementation;

import com.kairgaliyev.telegrambotdemo.command.CommandHandler;
import com.kairgaliyev.telegrambotdemo.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Base64;

/**
 * Класс с названием командой и описанием для загрузки эксле файла
 */
@Component
public class DownloadCommandHandler implements CommandHandler {
    @Autowired
    private CategoryService categoryService;
    private static final Logger logger = LoggerFactory.getLogger(DownloadCommandHandler.class);

    @Override
    public String getCommand() {
        return "/download";
    }

    @Override
    public String getDescription() {
        return "Скачать дерево в Excel (Файл не должен превышать 20мб)";
    }

    /**
     * Метод для создания эксел и отправки пользователю
     *
     * @param chatId идентификатор чата
     * @param args   аргументы команды
     * @return String
     */
    @Override
    public String handleCommand(Long chatId, String[] args) {
        try {
            byte[] excelData = categoryService.exportToExcel(chatId);
            logger.info("Вызван метод в классе DownloadCommandHandler, идентификатор чата: {}", chatId);
            return "FILE:" + Base64.getEncoder().encodeToString(excelData) + ":categories.xlsx";
        } catch (Exception e) {
            logger.error("Ошибка во время добавления элемента {}", e.getMessage());
            return "Ошибка при генерации файла";
        }
    }
}