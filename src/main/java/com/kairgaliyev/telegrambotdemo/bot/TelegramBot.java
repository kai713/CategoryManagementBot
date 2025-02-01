package com.kairgaliyev.telegrambotdemo.bot;

import com.kairgaliyev.telegrambotdemo.command.CommandHandler;
import com.kairgaliyev.telegrambotdemo.command.MessageSender;
import com.kairgaliyev.telegrambotdemo.command.implementation.UploadCommandHandler;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Основной класс Telegram-бота, реализующий взаимодействие с пользователями.
 * Этот класс наследуется от TelegramLongPollingBot и реализует интерфейс MessageSender.
 */
@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class TelegramBot extends TelegramLongPollingBot implements MessageSender {
    private final List<CommandHandler> commandHandlers;
    private final UploadCommandHandler uploadCommandHandler;
    private final Map<String, CommandHandler> commandMap = new HashMap<>();

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;

    /**
     * Инициализация команд бота после создания бина.
     * Загружает все обработчики команд в карту для быстрого доступа.
     */
    @PostConstruct
    public void init() {
        commandHandlers.forEach(h -> commandMap.put(h.getCommand(), h));
    }

    /**
     * Метод, вызываемый при получении обновления (нового сообщения или команды).
     *
     * @param update объект обновления, содержащий данные о сообщении.
     */
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            handleTextMessage(update.getMessage());
        } else if (update.hasMessage() && update.getMessage().hasDocument()) {
            handleDocumentMessage(update.getMessage());
        }
    }

    /**
     * Обрабатывает текстовые сообщения от пользователей.
     * Проверяет команду и вызывает соответствующий обработчик.
     *
     * @param message текстовое сообщение пользователя.
     */
    private void handleTextMessage(Message message) {
        String text = message.getText();
        String[] parts = text.split(" ", 2);
        String command = parts[0];
        String[] args = parts.length > 1 ? parts[1].split(" ") : new String[0];

        CommandHandler handler = commandMap.get(command);
        if (handler != null) {
            String result = handler.handleCommand(message.getChatId(), args);
            processResult(message.getChatId(), result);
        } else {
            sendMessage(message.getChatId(), "Неизвестная команда. Используйте /help");
        }
    }

    /**
     * Обрабатывает результат выполнения команды.
     * Если результат представляет собой файл, отправляет его пользователю.
     *
     * @param chatId ID чата пользователя.
     * @param result строка-результат выполнения команды.
     */
    private void processResult(Long chatId, String result) {
        if (result.startsWith("FILE:")) {
            String[] parts = result.split(":");
            if (parts.length == 3) {
                byte[] fileData = Base64.getDecoder().decode(parts[1]);
                String fileName = parts[2];
                sendDocument(chatId, fileData, fileName);
                return;
            }
        }
        sendMessage(chatId, result);
    }

    /**
     * Отправляет документ пользователю.
     *
     * @param chatId ID чата пользователя.
     * @param data массив байтов документа.
     * @param fileName имя файла.
     */
    private void sendDocument(Long chatId, byte[] data, String fileName) {
        SendDocument document = new SendDocument();
        document.setChatId(chatId.toString());
        document.setDocument(new InputFile(new ByteArrayInputStream(data), fileName));
        try {
            execute(document);
        } catch (TelegramApiException e) {
            sendMessage(chatId, "Ошибка отправки файла: " + e.getMessage());
        }
    }

    /**
     * Отправляет текстовое сообщение пользователю.
     *
     * @param chatId ID чата пользователя.
     * @param text текст сообщения.
     */
    @Override
    public void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.fillInStackTrace();
        }
    }

    /**
     * Обрабатывает загруженные пользователем файлы.
     * Принимает только файлы формата .xlsx.
     *
     * @param message сообщение с документом.
     */
    private void handleDocumentMessage(Message message) {
        Document doc = message.getDocument();
        if (!doc.getFileName().endsWith(".xlsx")) {
            sendMessage(message.getChatId(), "Поддерживаются только файлы .xlsx");
            return;
        }

        try (InputStream fileStream = downloadFile(doc)) {
            String result = uploadCommandHandler.handleDocument(message.getChatId(), fileStream);
            sendMessage(message.getChatId(), result);
        } catch (Exception e) {
            sendMessage(message.getChatId(), "Ошибка: " + e.getMessage());
        }
    }

    /**
     * Загружает файл с серверов Telegram.
     *
     * @param document документ, отправленный пользователем.
     * @return поток ввода с данными файла.
     * @throws TelegramApiException если произошла ошибка API Telegram.
     * @throws IOException если произошла ошибка ввода-вывода.
     */
    private InputStream downloadFile(Document document) throws TelegramApiException, IOException {
        GetFile getFile = new GetFile();
        getFile.setFileId(document.getFileId());
        File file = execute(getFile);
        return new URL(file.getFileUrl(getBotToken())).openStream();
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
