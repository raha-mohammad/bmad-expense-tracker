package com.bmad.expensetracker.service;

import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// Icon values below are placeholders (icon key, not a rendered asset) - the real icon set is
// an explicitly deferred decision (ARCHITECTURE-SPINE.md #Deferred), resolved when apps/web's
// Categories screen is built in Epic 2.
@Component
public class CategoryBootstrapRunner implements CommandLineRunner {

    private static final List<String> DEFAULT_CATEGORY_NAMES =
            List.of("Food", "Transport", "Shopping", "Bills", "Entertainment");

    private final CategoryService categoryService;

    public CategoryBootstrapRunner(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // AD-4: this runner only talks to CategoryService - never CategoryRepository/JdbcTemplate
    // directly - even though it's a one-time startup task, not a request-handling path.
    @Override
    public void run(String... args) {
        categoryService.ensureSchemaHardened();
        for (String name : DEFAULT_CATEGORY_NAMES) {
            categoryService.ensureDefaultCategoryExists(name, name.toLowerCase());
        }
        categoryService.ensureSystemCategoryExists("Uncategorized", "help-circle");
    }
}
