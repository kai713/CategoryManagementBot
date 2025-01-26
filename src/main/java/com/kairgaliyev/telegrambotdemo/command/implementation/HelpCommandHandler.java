package com.kairgaliyev.telegrambotdemo.command.implementation;

import com.kairgaliyev.telegrambotdemo.command.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Команда для вывода списка всех команд
 */
@Component
public class HelpCommandHandler implements CommandHandler {
    private final List<CommandHandler> commandHandlers;

    @Autowired
    public HelpCommandHandler(List<CommandHandler> commandHandlers) {
        this.commandHandlers = commandHandlers;
    }

    @Override
    public String getCommand() {
        return "/help";
    }

    @Override
    public String getDescription() {
        return "Показать команды";
    }

    /**
     * Метод для вывода всех команд и их описании
     *
     * @param chatId идентификатор чата
     * @param args   аргументы команды
     * @return String
     */

    @Override
    public String handleCommand(Long chatId, String[] args) {
        StringBuilder helpText = new StringBuilder("📚 *Доступные команды:*\n\n");
        commandHandlers.forEach(handler -> {
            helpText.append("🔹 ")
                    .append(handler.getCommand())
                    .append(" - ")
                    .append(handler.getDescription())
                    .append("\n");
        });
        return helpText.toString();
    }
}