package com.kairgaliyev.telegrambotdemo.repository;

import com.kairgaliyev.telegrambotdemo.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);

    Optional<Category> findByNameAndChatId(String name, Long chatId);

    boolean existsByName(String name);

    void deleteByName(String name);

    boolean existsByNameAndChatId(String name, Long chatId);

    List<Category> findByParentIsNull();

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.children WHERE c.parent IS NULL")
    List<Category> findByParentIsNullWithChildren();

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.children WHERE c.parent IS NULL AND c.chatId = :chatId")

    List<Category> findByParentIsNullWithChildrenAndChatId(Long chatId);

    List<Category> findByChatId(Long chatId);

}

