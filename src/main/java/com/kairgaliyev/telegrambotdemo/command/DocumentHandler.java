package com.kairgaliyev.telegrambotdemo.command;

import java.io.InputStream;

public interface DocumentHandler {
    String handleDocument(Long chatId, InputStream inputStream);
}
