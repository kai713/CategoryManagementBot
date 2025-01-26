package com.kairgaliyev.telegrambotdemo.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(uniqueConstraints =
@UniqueConstraint(columnNames = {"name", "parent_id"}))
public class Category {

    /**
     * Category ID (primary key).
     * Value is generated automatically.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Category name.
     */
    @Column(unique = true)
    private String name;

    @Column(nullable = false)
    private Long chatId;

    /**
     * Parent category.
     * Many-to-One relationship, i.e. one category can have one parent.
     */
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    /**
     * List of child categories.
     * One-to-Many relationship, i.e. one category can have several child categories.
     * CascadeType.ALL means that all operations with the parent will also affect the child categories.
     * orphanRemoval = true means that if the child category is no longer associated with the parent, it will be removed.
     * FetchType.EAGER means that the child categories will be loaded immediately when the parent category is loaded.
     */
    @OneToMany(mappedBy = "parent",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            orphanRemoval = true)
    private List<Category> children = new ArrayList<>();


    /**
     * Constructors, getters and setters
     */
    public Category(String name) {
        this.name = name;
    }

    public Category() {
    }

    public Category(String name, Long chatId) {
        this.name = name;
        this.chatId = chatId;
    }

    public void addChild(Category child) {
        child.setParent(this);
        children.add(child);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public List<Category> getChildren() {
        return children;
    }

    public void setChildren(List<Category> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", parent=" + parent +
                ", children=" + children +
                '}';
    }
}