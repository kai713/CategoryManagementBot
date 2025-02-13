package com.kairgaliyev.telegrambotdemo.service.implementation;

import com.kairgaliyev.telegrambotdemo.entity.Category;
import com.kairgaliyev.telegrambotdemo.exception.CategoryException;
import com.kairgaliyev.telegrambotdemo.exception.CategoryNotFoundException;
import com.kairgaliyev.telegrambotdemo.repository.CategoryRepository;
import com.kairgaliyev.telegrambotdemo.service.ICategoryService;
import com.kairgaliyev.telegrambotdemo.util.Util;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

//TODO refactor
/**
 * Сервис для добавления, удаления и получения категории из базы данных.
 */
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class CategoryService implements ICategoryService {
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
    public String viewTree(Long chatId) {
        List<Category> roots = categoryRepository.findByParentIsNullWithChildrenAndChatId(chatId);
        if (roots.isEmpty()) return "Дерево категорий пусто";

        logger.info("Построение дерева с идентификатором чата: {}", chatId);
        StringBuilder sb = new StringBuilder();
        Util.buildTree(roots, "", sb);
        return sb.toString();
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