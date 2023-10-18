package com.armandoplacido.admin.catalogo.application.category.retrieve.list;

import com.armandoplacido.admin.catalogo.application.UseCase;
import com.armandoplacido.admin.catalogo.domain.category.CategorySearchQuery;
import com.armandoplacido.admin.catalogo.domain.pagination.Pagination;

public abstract class ListCategoriesUseCase extends UseCase<CategorySearchQuery, Pagination<CategoryListOutput>> {
}
