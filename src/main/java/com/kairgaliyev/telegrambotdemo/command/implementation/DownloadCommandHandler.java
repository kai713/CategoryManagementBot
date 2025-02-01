package com.kairgaliyev.telegrambotdemo.command.implementation;

import com.kairgaliyev.telegrambotdemo.command.CommandHandler;
import com.kairgaliyev.telegrambotdemo.service.implementation.CategoryExcelService;
import com.kairgaliyev.telegrambotdemo.service.implementation.CategoryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Base64;

/**
 * Класс с названием командой и описанием для загрузки эксле файла
 */
@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class DownloadCommandHandler implements CommandHandler {
    private static final Logger logger = LoggerFactory.getLogger(DownloadCommandHandler.class);
    private final CategoryExcelService categoryExcelService;

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
            byte[] excelData = categoryExcelService.exportToExcel(chatId);
            logger.info("Вызван метод в классе DownloadCommandHandler, идентификатор чата: {}", chatId);
            return "FILE:" + Base64.getEncoder().encodeToString(excelData) + ":categories.xlsx";
        } catch (Exception e) {
            logger.error("Ошибка во время добавления элемента {}", e.getMessage());
            return "Ошибка при генерации файла";
        }
    }
}