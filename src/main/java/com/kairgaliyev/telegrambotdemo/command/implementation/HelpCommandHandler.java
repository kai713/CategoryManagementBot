package com.kairgaliyev.telegrambotdemo.command.implementation;

import com.kairgaliyev.telegrambotdemo.command.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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

    @Override
    public String handleCommand(Long chatId, String[] args) {
        System.out.println("Found handlers: " + commandHandlers.size());
        commandHandlers.forEach(h ->
                System.out.println(h.getCommand() + " - " + h.getDescription()));

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