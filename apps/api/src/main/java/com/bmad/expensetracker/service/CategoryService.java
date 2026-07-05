package com.bmad.expensetracker.service;

import com.bmad.expensetracker.dto.CategoryDto;
import com.bmad.expensetracker.entity.Category;
import com.bmad.expensetracker.entity.CategoryKind;
import com.bmad.expensetracker.repository.CategoryRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;
    private final JdbcTemplate jdbcTemplate;

    public CategoryService(CategoryRepository categoryRepository, JdbcTemplate jdbcTemplate) {
        this.categoryRepository = categoryRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream().map(this::toDto).toList();
    }

    // AD-5's constraints (SYSTEM singleton, case-insensitive name uniqueness) can't be expressed
    // via JPA/Hibernate annotations, so they're applied here as native SQL, after Hibernate's
    // ddl-auto has already created the table during context startup. IF NOT EXISTS makes this
    // safe to re-run on every boot.
    public void ensureSchemaHardened() {
        jdbcTemplate.execute(
                "CREATE UNIQUE INDEX IF NOT EXISTS ux_categories_system_singleton "
                        + "ON categories (kind) WHERE kind = 'SYSTEM'");
        jdbcTemplate.execute(
                "CREATE UNIQUE INDEX IF NOT EXISTS ux_categories_name_lower "
                        + "ON categories (lower(name))");
    }

    // The empty-check-then-save is a race if two instances start concurrently; the unique index
    // (ensureSchemaHardened) is the real guarantee, this just avoids that race crashing startup.
    public void ensureDefaultCategoryExists(String name, String icon) {
        if (categoryRepository.findByNameIgnoreCase(name).isPresent()) {
            return;
        }
        try {
            categoryRepository.save(new Category(name, icon, CategoryKind.DEFAULT));
        } catch (DataIntegrityViolationException e) {
            log.debug("Category '{}' already seeded by a concurrent startup - ignoring", name);
        }
    }

    public void ensureSystemCategoryExists(String name, String icon) {
        if (categoryRepository.existsByKind(CategoryKind.SYSTEM)) {
            return;
        }
        try {
            categoryRepository.save(new Category(name, icon, CategoryKind.SYSTEM));
        } catch (DataIntegrityViolationException e) {
            log.debug("SYSTEM category already seeded by a concurrent startup - ignoring");
        }
    }

    private CategoryDto toDto(Category category) {
        return new CategoryDto(
                category.getId(), category.getName(), category.getIcon(), category.getKind().name());
    }
}
