package com.kairgaliyev.telegrambotdemo.util;

import com.kairgaliyev.telegrambotdemo.entity.Category;
import org.apache.poi.ss.usermodel.Cell;

import java.util.List;

public class Util {
    /**
     * Рекурсивный метод для построения дерева с помощью StringBuilder
     * Используются символы для визуального отоброжения
     *
     * @param categories список категории
     * @param indent абзац
     * @param sb ссылка на стринг билдер
     */
    public static void buildTree(List<Category> categories, String indent, StringBuilder sb) {
        for (int i = 0; i < categories.size(); i++) {
            Category category = categories.get(i);
            boolean isLast = i == categories.size() - 1;

            sb.append(indent)
                    .append(isLast ? "└─ " : "├─ ")
                    .append(category.getName())
                    .append("\n");

            if (!category.getChildren().isEmpty()) {
                buildTree(category.getChildren(),
                        indent + (isLast ? "   " : "│  "),
                        sb);
            }
        }
    }

    /**
     * Вспомогательный метод для чтение и обработки ячеек
     * @param cell ячейка
     * @return String
     */
    public static String getCellStringValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            default -> "";
        };
    }
}
