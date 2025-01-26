package com.kairgaliyev.telegrambotdemo.exception;

/**
 * Собственный класс исключения для улучшения читаемости, поддержки и управление ошибками.
 * Наследует класс CategoryException
 */
public class CategoryNotFoundException extends CategoryException {
    public CategoryNotFoundException(String message) {
        super(message);
    }
}
