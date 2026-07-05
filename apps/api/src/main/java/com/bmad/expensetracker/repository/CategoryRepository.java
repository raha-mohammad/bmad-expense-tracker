package com.bmad.expensetracker.repository;

import com.bmad.expensetracker.entity.Category;
import com.bmad.expensetracker.entity.CategoryKind;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByKind(CategoryKind kind);

    Optional<Category> findByNameIgnoreCase(String name);
}
