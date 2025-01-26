package com.kairgaliyev.telegrambotdemo.exception;

/**
 * Собственный класс исключения для улучшения читаемости, поддержки и управление ошибками.
 */
public class CategoryException extends Exception {
    public CategoryException(String message) {
        super(message);
    }
}
