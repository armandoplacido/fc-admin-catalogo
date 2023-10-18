package com.armandoplacido.admin.catalogo.infrastructure.category;

import com.armandoplacido.admin.catalogo.domain.category.Category;
import com.armandoplacido.admin.catalogo.domain.category.CategoryID;
import com.armandoplacido.admin.catalogo.domain.category.CategorySearchQuery;
import com.armandoplacido.admin.catalogo.domain.pagination.Pagination;
import com.armandoplacido.admin.catalogo.MySQLGatewayTest;
import com.armandoplacido.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.armandoplacido.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@MySQLGatewayTest
public class CategoryMTSQLGatewayTest {
    @Autowired
    private CategoryMYSQLGateway categoryGateway;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void givenAValidCategory_whenCallsCreate_shouldReturnANewCategory() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        Category aCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        Assertions.assertEquals(0, categoryRepository.count());

        final var actualCategory = categoryGateway.create(aCategory);

        Assertions.assertEquals(1, categoryRepository.count());

        Assertions.assertEquals(aCategory.getId(), actualCategory.getId());
        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertEquals(aCategory.getCreatedAt(), actualCategory.getCreatedAt());
        Assertions.assertEquals(aCategory.getUpdatedAt(), actualCategory.getUpdatedAt());
        Assertions.assertEquals(aCategory.getDeletedAt(), actualCategory.getDeletedAt());
        Assertions.assertNull(actualCategory.getDeletedAt());

        final var actualEntity = categoryRepository.findById(aCategory.getId().getValue()).get();

        Assertions.assertEquals(aCategory.getId().getValue(), actualEntity.getId());
        Assertions.assertEquals(expectedName, actualEntity.getName());
        Assertions.assertEquals(expectedDescription, actualEntity.getDescription());
        Assertions.assertEquals(expectedIsActive, actualEntity.isActive());
        Assertions.assertEquals(aCategory.getCreatedAt(), actualEntity.getCreatedAt());
        Assertions.assertEquals(aCategory.getUpdatedAt(), actualEntity.getUpdatedAt());
        Assertions.assertEquals(aCategory.getDeletedAt(), actualEntity.getDeletedAt());
        Assertions.assertNull(actualEntity.getDeletedAt());
    }

    @Test
    public void givenAValidCategory_whenCallsUpdate_shouldReturnCategoryUpdated() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        Category aCategory = Category.newCategory("Film", null, expectedIsActive);

        Assertions.assertEquals(0, categoryRepository.count());

        categoryRepository.saveAndFlush(CategoryJpaEntity.from(aCategory));

        Assertions.assertEquals(1, categoryRepository.count());

        final var actualOldEntity = categoryRepository.findById(aCategory.getId().getValue()).get();

        Assertions.assertEquals("Film", actualOldEntity.getName());
        Assertions.assertNull( actualOldEntity.getDescription());
        Assertions.assertEquals(expectedIsActive, actualOldEntity.isActive());

        final var aUpdatedCategory =
                aCategory.clone().update(expectedName, expectedDescription, expectedIsActive);

        final var actualCategory = categoryGateway.update(aUpdatedCategory);

        Assertions.assertEquals(1, categoryRepository.count());

        Assertions.assertEquals(aCategory.getId(), actualCategory.getId());
        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertEquals(aCategory.getCreatedAt(), actualCategory.getCreatedAt());
        Assertions.assertTrue(aCategory.getUpdatedAt().isBefore(actualCategory.getUpdatedAt()));
        Assertions.assertEquals(aCategory.getDeletedAt(), actualCategory.getDeletedAt());
        Assertions.assertNull(actualCategory.getDeletedAt());

        final var actualEntity = categoryRepository.findById(aCategory.getId().getValue()).get();

        Assertions.assertEquals(aCategory.getId().getValue(), actualEntity.getId());
        Assertions.assertEquals(expectedName, actualEntity.getName());
        Assertions.assertEquals(expectedDescription, actualEntity.getDescription());
        Assertions.assertEquals(expectedIsActive, actualEntity.isActive());
        Assertions.assertEquals(aCategory.getCreatedAt(), actualEntity.getCreatedAt());
        Assertions.assertTrue(aCategory.getUpdatedAt().isBefore(actualCategory.getUpdatedAt()));
        Assertions.assertEquals(aCategory.getDeletedAt(), actualEntity.getDeletedAt());
        Assertions.assertNull(actualEntity.getDeletedAt());
    }

    @Test
    public void givenAPrePersistedCategoryAndValidCategoryId_whenTryDeleteIt_shouldDeleteCategory() {
        final var aCategory = Category.newCategory("Filmes", null, true);

        Assertions.assertEquals(0, categoryRepository.count());

        categoryRepository.saveAndFlush(CategoryJpaEntity.from(aCategory));

        Assertions.assertEquals(1, categoryRepository.count());

        categoryGateway.deleteById(aCategory.getId());

        Assertions.assertEquals(0, categoryRepository.count());
    }

    @Test
    public void givenAPrePersistedCategoryAndInvalidCategoryId_whenTryDeleteIt_shouldDeleteCategory() {
        Assertions.assertEquals(0, categoryRepository.count());

        categoryGateway.deleteById(CategoryID.from("invalid"));

        Assertions.assertEquals(0, categoryRepository.count());
    }

    @Test
    public void givenAPrePersistedCategoryAndValidCategoryId_whenCallsFindById_shouldReturnCategory() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        Category aCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        Assertions.assertEquals(0, categoryRepository.count());

        categoryRepository.saveAndFlush(CategoryJpaEntity.from(aCategory));

        Assertions.assertEquals(1, categoryRepository.count());

        final var actualCategory = categoryGateway.findById(aCategory.getId()).get();

        Assertions.assertEquals(1, categoryRepository.count());

        Assertions.assertEquals(aCategory.getId(), actualCategory.getId());
        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertEquals(aCategory.getCreatedAt(), actualCategory.getCreatedAt());
        Assertions.assertEquals(aCategory.getUpdatedAt(),actualCategory.getUpdatedAt());
        Assertions.assertEquals(aCategory.getDeletedAt(), actualCategory.getDeletedAt());
        Assertions.assertNull(actualCategory.getDeletedAt());
    }

    @Test
    public void givenValidCategoryIdNotStorage_whenCallsFindById_shouldReturnEmpty() {
        Assertions.assertEquals(0, categoryRepository.count());

        Optional<Category> actualCategory = categoryGateway.findById(CategoryID.from("empty"));

        Assertions.assertTrue(actualCategory.isEmpty());
    }

    @Test
    public void givenPrePresistedCategories_whenCallsFindAll_shouldReturnPaginated() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 3;

        final var filmes = Category.newCategory("Filmes", null, true);
        final var series = Category.newCategory("Séries", null, true);
        final var documentarios = Category.newCategory("Documentários", null, true);

        Assertions.assertEquals(0, categoryRepository.count());

        categoryRepository.saveAll(List.of(
                CategoryJpaEntity.from(filmes),
                CategoryJpaEntity.from(series),
                CategoryJpaEntity.from(documentarios)
        ));

        Assertions.assertEquals(3,categoryRepository.count());

        final var query = new CategorySearchQuery(0,1,"", "name", "asc");

        Pagination<Category> actualResul = categoryGateway.findAll(query);

        Assertions.assertEquals(expectedPage, actualResul.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResul.perPage());
        Assertions.assertEquals(expectedTotal, actualResul.total());
        Assertions.assertEquals(expectedPerPage, actualResul.items().size());
        Assertions.assertEquals(documentarios.getId(), actualResul.items().get(0).getId());
    }

    @Test
    public void givenEmptyCategoriesTable_whenCallsFindAll_shouldReturnEmptyPage() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 0;

        Assertions.assertEquals(0, categoryRepository.count());

        final var query = new CategorySearchQuery(0,1,"", "name", "asc");

        Pagination<Category> actualResul = categoryGateway.findAll(query);

        Assertions.assertEquals(expectedPage, actualResul.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResul.perPage());
        Assertions.assertEquals(expectedTotal, actualResul.total());
        Assertions.assertEquals(0, actualResul.items().size());
    }

    @Test
    public void givenFollowPagination_whenCallsFindAllWithPage1_shouldReturnPaginated() {
        var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 3;

        final var filmes = Category.newCategory("Filmes", null, true);
        final var series = Category.newCategory("Séries", null, true);
        final var documentarios = Category.newCategory("Documentários", null, true);

        Assertions.assertEquals(0, categoryRepository.count());

        categoryRepository.saveAll(List.of(
                CategoryJpaEntity.from(filmes),
                CategoryJpaEntity.from(series),
                CategoryJpaEntity.from(documentarios)
        ));

        Assertions.assertEquals(3,categoryRepository.count());

        var query = new CategorySearchQuery(0,1,"", "name", "asc");
        var actualResul = categoryGateway.findAll(query);

        Assertions.assertEquals(expectedPage, actualResul.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResul.perPage());
        Assertions.assertEquals(expectedTotal, actualResul.total());
        Assertions.assertEquals(expectedPerPage, actualResul.items().size());
        Assertions.assertEquals(documentarios.getId(), actualResul.items().get(0).getId());

        // Page 1
        expectedPage = 1;
        query = new CategorySearchQuery(1,1,"", "name", "asc");
        actualResul = categoryGateway.findAll(query);

        Assertions.assertEquals(expectedPage, actualResul.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResul.perPage());
        Assertions.assertEquals(expectedTotal, actualResul.total());
        Assertions.assertEquals(expectedPerPage, actualResul.items().size());
        Assertions.assertEquals(filmes.getId(), actualResul.items().get(0).getId());

        // Page 2
        expectedPage = 2;
        query = new CategorySearchQuery(2,1,"", "name", "asc");
        actualResul = categoryGateway.findAll(query);

        Assertions.assertEquals(expectedPage, actualResul.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResul.perPage());
        Assertions.assertEquals(expectedTotal, actualResul.total());
        Assertions.assertEquals(expectedPerPage, actualResul.items().size());
        Assertions.assertEquals(series.getId(), actualResul.items().get(0).getId());
    }

    @Test
    public void givenPrePresistedCategoriesAndDocAsTerms_whenCallsFindAllAndTermsMatchsCategoryName_shouldReturnPaginated() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 1;

        final var filmes = Category.newCategory("Filmes", null, true);
        final var series = Category.newCategory("Séries", null, true);
        final var documentarios = Category.newCategory("Documentários", null, true);

        Assertions.assertEquals(0, categoryRepository.count());

        categoryRepository.saveAll(List.of(
                CategoryJpaEntity.from(filmes),
                CategoryJpaEntity.from(series),
                CategoryJpaEntity.from(documentarios)
        ));

        Assertions.assertEquals(3,categoryRepository.count());

        final var query = new CategorySearchQuery(0,1,"doc", "name", "asc");

        Pagination<Category> actualResul = categoryGateway.findAll(query);

        Assertions.assertEquals(expectedPage, actualResul.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResul.perPage());
        Assertions.assertEquals(expectedTotal, actualResul.total());
        Assertions.assertEquals(expectedPerPage, actualResul.items().size());
        Assertions.assertEquals(documentarios.getId(), actualResul.items().get(0).getId());
    }

    @Test
    public void givenPrePresistedCategoriesAndMaisAssistidaAsTerms_whenCallsFindAllAndTermsMatchsCategoryDescription_shouldReturnPaginated() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 1;

        final var filmes = Category.newCategory("Filmes", "A categoria mais assistida", true);
        final var series = Category.newCategory("Séries", "Uma categoria assistida", true);
        final var documentarios = Category.newCategory("Documentários", "A categoria menos assistida", true);

        Assertions.assertEquals(0, categoryRepository.count());

        categoryRepository.saveAll(List.of(
                CategoryJpaEntity.from(filmes),
                CategoryJpaEntity.from(series),
                CategoryJpaEntity.from(documentarios)
        ));

        Assertions.assertEquals(3,categoryRepository.count());

        final var query = new CategorySearchQuery(0,1,"MAIS ASSISTIDA", "description", "asc");

        Pagination<Category> actualResul = categoryGateway.findAll(query);

        Assertions.assertEquals(expectedPage, actualResul.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResul.perPage());
        Assertions.assertEquals(expectedTotal, actualResul.total());
        Assertions.assertEquals(expectedPerPage, actualResul.items().size());
        Assertions.assertEquals(filmes.getId(), actualResul.items().get(0).getId());
    }
}
