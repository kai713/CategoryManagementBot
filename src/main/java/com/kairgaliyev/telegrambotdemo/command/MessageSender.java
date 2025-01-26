package com.kairgaliyev.telegrambotdemo.command;

public interface MessageSender {
    void sendMessage(Long chatId, String text);
}
