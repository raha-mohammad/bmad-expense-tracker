package com.bmad.expensetracker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Case-insensitive name uniqueness and the SYSTEM-row singleton (AD-5) cannot be expressed
// via JPA annotations - they're enforced by native SQL indexes created in CategoryBootstrapRunner.
// A plain @Column(unique = true) is intentionally omitted here since it would be case-sensitive
// and misleadingly suggest the constraint lives at this layer.
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String icon;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private CategoryKind kind;

    protected Category() {
        // JPA
    }

    public Category(String name, String icon, CategoryKind kind) {
        this.name = name;
        this.icon = icon;
        this.kind = kind;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public CategoryKind getKind() {
        return kind;
    }
}
