package com.kairgaliyev.telegrambotdemo.command.implementation;

import com.kairgaliyev.telegrambotdemo.command.CommandHandler;
import org.springframework.stereotype.Component;

/**
 * Класс с названием командой и описанием для начало работы с ботом и приветствия
 */
@Component
public class StartCommandHandler implements CommandHandler {
    @Override
    public String getCommand() {
        return "/start";
    }

    @Override
    public String getDescription() {
        return "Команда для начало работы с телеграмм ботом";
    }

    /**
     * Метод для приветствия и дальнейшей работы с этим ботом
     * @param chatId идентификатор чата
     * @param args аргументы
     * @return String
     */
    @Override
    public String handleCommand(Long chatId, String[] args) {
        return "Добро пожаловать в бот для управления и построение иерархии категории, список всех команд можно просмотреть с помощью команды /help";
    }
}
