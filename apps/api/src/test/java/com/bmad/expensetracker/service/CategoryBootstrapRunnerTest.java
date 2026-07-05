package com.bmad.expensetracker.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.bmad.expensetracker.entity.Category;
import com.bmad.expensetracker.entity.CategoryKind;
import com.bmad.expensetracker.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

// AC3: exactly 5 DEFAULT + 1 SYSTEM category, seeded idempotently - re-running never duplicates.
// The seeder already ran once during this SpringBootTest's own context startup; this test
// re-invokes it explicitly to prove a second run is a no-op.
@SpringBootTest
class CategoryBootstrapRunnerTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryBootstrapRunner categoryBootstrapRunner;

    @Test
    void seedingIsIdempotentAcrossRepeatedRuns() {
        long countAfterContextStartup = categoryRepository.count();
        assertThat(countAfterContextStartup).isEqualTo(6);
        assertThat(categoryRepository.findAll().stream().filter(c -> c.getKind() == CategoryKind.DEFAULT).count())
                .isEqualTo(5);
        assertThat(categoryRepository.findAll().stream().filter(c -> c.getKind() == CategoryKind.SYSTEM).count())
                .isEqualTo(1);

        categoryBootstrapRunner.run();
        categoryBootstrapRunner.run();

        assertThat(categoryRepository.count()).isEqualTo(countAfterContextStartup);
    }

    @Test
    void defaultCategoryNamesMatchTheSpec() {
        var names = categoryRepository.findAll().stream()
                .filter(c -> c.getKind() == CategoryKind.DEFAULT)
                .map(Category::getName)
                .toList();
        assertThat(names).containsExactlyInAnyOrder("Food", "Transport", "Shopping", "Bills", "Entertainment");
    }

    @Test
    void systemCategoryIsNamedUncategorized() {
        var system = categoryRepository.findAll().stream()
                .filter(c -> c.getKind() == CategoryKind.SYSTEM)
                .findFirst()
                .orElseThrow();
        assertThat(system.getName()).isEqualTo("Uncategorized");
    }
}
