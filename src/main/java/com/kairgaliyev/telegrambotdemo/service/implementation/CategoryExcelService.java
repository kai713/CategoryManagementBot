package com.kairgaliyev.telegrambotdemo.service.implementation;

import com.kairgaliyev.telegrambotdemo.entity.Category;
import com.kairgaliyev.telegrambotdemo.repository.CategoryRepository;
import com.kairgaliyev.telegrambotdemo.service.ICategoryExcelService;
import com.kairgaliyev.telegrambotdemo.util.Util;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryExcelService implements ICategoryExcelService {
    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;

    private static final Logger logger = LoggerFactory.getLogger(CategoryExcelService.class);

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

            String categoryName = Util.getCellStringValue(row.getCell(0));
            String parentName = Util.getCellStringValue(row.getCell(1));

            if (categoryName.isBlank()) continue;

            if (categoryRepository.findByNameAndChatId(categoryName, chatId).isEmpty()) {
                if (parentName.isBlank()) {
                    categoryService.addRootCategory(categoryName, chatId);
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
}
