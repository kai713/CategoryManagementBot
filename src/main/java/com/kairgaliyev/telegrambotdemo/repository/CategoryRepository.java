package com.kairgaliyev.telegrambotdemo.repository;

import
        com.kairgaliyev.telegrambotdemo.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностью Category.
 * Содержит методы для поиска категорий по различным критериям.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Метод для поиска категории по имени и по идентификатору чата.
     *
     * @param name   название категории
     * @param chatId идентификатор чата
     * @return Optional<Category>
     */
    Optional<Category> findByNameAndChatId(String name, Long chatId);

    /**
     * Метод для проверки существования категории по имени и по идентификатору чата.
     *
     * @param name   название категории
     * @param chatId идентификатор чата
     * @return boolean
     */
    boolean existsByNameAndChatId(String name, Long chatId);

    /**
     * Метод для поиска всех категории.
     *
     * @param chatId идентификатор чата
     *               return List<Category>
     */
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.children WHERE c.parent IS NULL AND c.chatId = :chatId")
    List<Category> findByParentIsNullWithChildrenAndChatId(Long chatId);

    /**
     * Метод для поиска всех категории по идентификатору чата.
     *
     * @param chatId идентификатор чата
     * @return List<Category>
     */
    List<Category> findByChatId(Long chatId);

}

