package com.kairgaliyev.telegrambotdemo.service;

import com.kairgaliyev.telegrambotdemo.entity.Category;
import com.kairgaliyev.telegrambotdemo.exception.CategoryNotFoundException;
import com.kairgaliyev.telegrambotdemo.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public Category addRootCategory(String name, Long chatId) throws Exception {

        if (categoryRepository.existsByName(name)) {
            throw new Exception("Category already exists");
        }
        Category category = new Category(name, chatId);
        return categoryRepository.save(category);
    }

    @Transactional
    public Category addChildCategory(String parentName, String childName, Long chatId) throws Exception {
        Category parent = categoryRepository.findByNameAndChatId(parentName, chatId)
                .orElseThrow(() -> new Exception("Родительская категория не найдена: " + parentName));

        boolean childExists = parent.getChildren().stream()
                .anyMatch(c -> c.getName().equalsIgnoreCase(childName));

        if (childExists) {
            throw new Exception("Дочерняя категория уже существует у этого родителя");
        }

        Category child = categoryRepository.findByNameAndChatId(childName, chatId)
                .orElse(new Category(childName, chatId));

        if (child.getId() != null) {
            if (child.getParent() != null) {
                child.getParent().getChildren().remove(child);
            }
            child.setParent(parent);
        } else {
            parent.addChild(child);
        }

        categoryRepository.save(child);
        return child;
    }

    @Transactional
    public void removeCategory(String name, Long chatId) throws CategoryNotFoundException {
        Category category = categoryRepository.findByNameAndChatId(name, chatId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found: " + name));

        if (category.getParent() != null) {
            category.getParent().getChildren().remove(category);
        }

        categoryRepository.delete(category);
    }

    public String buildTree(Long chatId) {
        List<Category> roots = categoryRepository.findByParentIsNullWithChildrenAndChatId(chatId);
        if (roots.isEmpty()) return "Дерево категорий пусто";

        StringBuilder sb = new StringBuilder();
        buildTree(roots, "", sb);
        return sb.toString();
    }

    private void buildTree(List<Category> categories, String indent, StringBuilder sb) {
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

    // CategoryService.java
    public byte[] exportToExcel(Long chatId) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Categories");

            // Заголовки
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Категория");
            headerRow.createCell(1).setCellValue("Родительская категория");

            // Данные
            List<Category> allCategories = categoryRepository.findByChatId(chatId);
            int rowNum = 1;
            for (Category category : allCategories) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(category.getName());
                row.createCell(1).setCellValue(
                        category.getParent() != null ? category.getParent().getName() : ""
                );
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    @Transactional
    public void importFromExcel(InputStream inputStream, Long chatId) throws Exception {
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue;

            String categoryName = getCellStringValue(row.getCell(0));
            String parentName = getCellStringValue(row.getCell(1));

            if (categoryName.isBlank()) continue;

            if (categoryRepository.findByNameAndChatId(categoryName, chatId).isEmpty()) {
                if (parentName.isBlank()) {
                    addRootCategory(categoryName, chatId);
                } else {
                    Category parent = categoryRepository.findByNameAndChatId(parentName, chatId)
                            .orElseGet(() -> categoryRepository.save(new Category(parentName, chatId)));

                    if (!categoryRepository.existsByNameAndChatId(categoryName, chatId)) {
                        parent.addChild(new Category(categoryName, chatId));
                        categoryRepository.save(parent);
                    }
                }
            }
        }
        workbook.close();
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            default -> "";
        };
    }

    public boolean existsByNameAndChatId(String name, Long chatId) {
        return categoryRepository.existsByNameAndChatId(name, chatId);
    }
}