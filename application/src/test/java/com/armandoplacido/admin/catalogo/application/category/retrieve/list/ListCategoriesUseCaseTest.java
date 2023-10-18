package com.armandoplacido.admin.catalogo.application.category.retrieve.list;

import com.armandoplacido.admin.catalogo.domain.category.Category;
import com.armandoplacido.admin.catalogo.domain.category.CategoryGateway;
import com.armandoplacido.admin.catalogo.domain.category.CategorySearchQuery;
import com.armandoplacido.admin.catalogo.domain.pagination.Pagination;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ListCategoriesUseCaseTest {

    @InjectMocks
    private DefaultListCategoriesUseCase useCase;

    @Mock
    private CategoryGateway categoryGateway;


    @BeforeEach
    void cleanUp() {
        Mockito.reset(categoryGateway);
    }


    @Test
    public void givenAValidQuery_whenCallsListCategories_thenShouldReturnCategories() {
        final var categories = List.of(
                Category.newCategory("Filmes", null, true),
                Category.newCategory("Series", null, true)
        );

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";


        final var aQuery =
                new CategorySearchQuery(expectedPage,expectedPerPage,expectedTerms,expectedSort,expectedDirection);


        final var expectedPagination = new Pagination<>(expectedPage,expectedPerPage,categories.size(),categories);

        final var expectedItemsCount = 2;
        final var expectedResult = expectedPagination.map(CategoryListOutput::from);

        Mockito.when(categoryGateway.findAll(Mockito.eq(aQuery)))
                .thenReturn(expectedPagination);

        final var actualResut = useCase.execute(aQuery);

        Assertions.assertEquals(expectedItemsCount,actualResut.items().size());
        Assertions.assertEquals(expectedResult, actualResut);
        Assertions.assertEquals(expectedPage,actualResut.currentPage());
        Assertions.assertEquals(expectedPerPage,actualResut.perPage());
        Assertions.assertEquals(categories.size(),actualResut.total());
    }

    @Test
    public  void givenAValidQuery_whenHasNoResults_thenShouldReturnEmptyCategories() {
        final var categories = List.<Category>of();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";


        final var aQuery =
                new CategorySearchQuery(expectedPage,expectedPerPage,expectedTerms,expectedSort,expectedDirection);


        final var expectedPagination = new Pagination<>(expectedPage,expectedPerPage,categories.size(),categories);

        final var expectedItemsCount = 0;
        final var expectedResult = expectedPagination.map(CategoryListOutput::from);

        Mockito.when(categoryGateway.findAll(Mockito.eq(aQuery)))
                .thenReturn(expectedPagination);

        final var actualResut = useCase.execute(aQuery);

        Assertions.assertEquals(expectedItemsCount,actualResut.items().size());
        Assertions.assertEquals(expectedResult, actualResut);
        Assertions.assertEquals(expectedPage,actualResut.currentPage());
        Assertions.assertEquals(expectedPerPage,actualResut.perPage());
        Assertions.assertEquals(categories.size(),actualResut.total());
    }

    @Test
    public void givenAValidQuery_whenGatewayThrowsException_shouldReturnException() {
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";

        final var expectedErrorMessage = "Gateway error";

        final var aQuery =
                new CategorySearchQuery(expectedPage,expectedPerPage,expectedTerms,expectedSort,expectedDirection);

        Mockito.when(categoryGateway.findAll(Mockito.eq(aQuery)))
                .thenThrow(new IllegalStateException(expectedErrorMessage));

        final var actualException =
                Assertions.assertThrows(IllegalStateException.class, () -> useCase.execute(aQuery));

        Assertions.assertEquals(expectedErrorMessage,actualException.getMessage());
    }

}
