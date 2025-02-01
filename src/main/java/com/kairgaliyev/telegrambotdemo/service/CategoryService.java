package com.kairgaliyev.telegrambotdemo.service;

import com.kairgaliyev.telegrambotdemo.entity.Category;
import com.kairgaliyev.telegrambotdemo.exception.CategoryException;
import com.kairgaliyev.telegrambotdemo.exception.CategoryNotFoundException;
import com.kairgaliyev.telegrambotdemo.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

//TODO refactor
/**
 * Сервис для добавления, удаления и получения категории из базы данных.
 */
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class CategoryService {
    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;

    /**
     * Метод для добавления корневой категории
     * @param name название категории
     * @param chatId идентификатор чата
     * @return Category
     * @throws CategoryNotFoundException
     */
    @Transactional
    public Category addRootCategory(String name, Long chatId) throws CategoryNotFoundException {

        if (categoryRepository.existsByNameAndChatId(name, chatId)) {
            throw new CategoryNotFoundException("Категория уже существует");
        }
        Category category = new Category(name, chatId);

        logger.info("{} добавлен как корневой элемент, идентификатор чата: {}", category, chatId);
        return categoryRepository.save(category);
    }

    /**
     * Метод для добавления дочерней категории к родительской категории
     * @param parentName название родительской категории
     * @param childName название дочериной катеогории
     * @param chatId идентификатор чата
     * @return Category
     * @throws CategoryException
     */
    @Transactional
    public Category addChildCategory(String parentName, String childName, Long chatId) throws CategoryException {
        Category parent = categoryRepository.findByNameAndChatId(parentName, chatId)
                .orElseThrow(() -> new CategoryNotFoundException("Родительская категория не найдена: " + parentName));

        boolean childExists = parent.getChildren().stream()
                .anyMatch(c -> c.getName().equalsIgnoreCase(childName));

        if (childExists) {
            //TODO new InvalidCategoryException
            throw new CategoryException("Дочерняя категория уже существует у этого родителя");
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
        logger.info("Дочерна категория: {} добавлена в родитель: {}", parentName, childName);

        return child;
    }

    /**
     * Метод для удаления категории по названию и по идентификатору чата
     * @param name название категории
     * @param chatId идентификатор чата
     * @throws CategoryNotFoundException
     */
    @Transactional
    public void removeCategory(String name, Long chatId) throws CategoryNotFoundException {
        Category category = categoryRepository.findByNameAndChatId(name, chatId)
                .orElseThrow(() -> new CategoryNotFoundException("Категория не найдена : " + name));

        if (category.getParent() != null) {
            category.getParent().getChildren().remove(category);
        }

        logger.info("Удалена категория: {} идентификатор чата: {}", name, chatId);
        categoryRepository.delete(category);
    }

    /**
     * Метод для построения дерева на основе всех категории у определенного пользователя
     * @param chatId идентификатор чата
     * @return String строковая представление иерархии категории
     */
    public String buildTree(Long chatId) {
        List<Category> roots = categoryRepository.findByParentIsNullWithChildrenAndChatId(chatId);
        if (roots.isEmpty()) return "Дерево категорий пусто";

        logger.info("Построение дерева с идентификатором чата: {}", chatId);
        StringBuilder sb = new StringBuilder();
        buildTree(roots, "", sb);
        return sb.toString();
    }

    /**
     * Вспомогательный метод для построения дерева с помощью StringBuilder
     * @param categories список категории
     * @param indent абзац
     * @param sb ссылка на стринг билдер
     */
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

    /**
     * Метод для создания эксел файла на основе всех категории у пользователя с определенным идентификатором
     * @param chatId идентификатор чата
     * @return byte[] массив байтов
     * @throws IOException
     */
    public byte[] exportToExcel(Long chatId) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            logger.info("Начало создание эксел файла, идентификатор чата: {}", chatId);
            Sheet sheet = workbook.createSheet("Categories");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Категория");
            headerRow.createCell(1).setCellValue("Родительская категория");

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
            logger.info("Завершение создание эксел файла, идентификатор чата: {}", chatId);
            return outputStream.toByteArray();
        }
    }

    /**
     * Метод для импорта эксел файла с чата
     * @param inputStream входящий поток данных
     * @param chatId идентификатор чата
     * @throws Exception
     */
    @Transactional
    public void importFromExcel(InputStream inputStream, Long chatId) throws Exception {
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        logger.info("Начало импорта эксел файла, идентификатор чата: {}", chatId);
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
        logger.info("Завершение импорта эксел файла, идентификатор чата: {}", chatId);
        workbook.close();
    }

    /**
     * Вспомогательный метод для чтение и обработки ячеек
     * @param cell ячейка
     * @return String
     */
    private String getCellStringValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            default -> "";
        };
    }

    /**
     * Метод для проверки на существования категории
     * @param name название категории
     * @param chatId идентификатор чата
     * @return boolean
     */
    public boolean existsByNameAndChatId(String name, Long chatId) {
        return categoryRepository.existsByNameAndChatId(name, chatId);
    }
}