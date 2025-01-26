package com.kairgaliyev.telegrambotdemo.command;

public interface CommandHandler {
    String getCommand();

    String getDescription();

    String handleCommand(Long chatId, String[] args);
}
