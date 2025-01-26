package com.kairgaliyev.telegrambotdemo.command;

public interface CommandHandler {
    String getCommand();

    String getDescription();

    //    void handleCommand(Message message, String[] args);
    String handleCommand(Long chatId, String[] args);
}
