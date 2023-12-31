package com.armandoplacido.admin.catalogo.infrastructure.category;

import com.armandoplacido.admin.catalogo.domain.category.Category;
import com.armandoplacido.admin.catalogo.domain.category.CategoryGateway;
import com.armandoplacido.admin.catalogo.domain.category.CategoryID;
import com.armandoplacido.admin.catalogo.domain.category.CategorySearchQuery;
import com.armandoplacido.admin.catalogo.domain.pagination.Pagination;
import com.armandoplacido.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.armandoplacido.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import com.armandoplacido.admin.catalogo.infrastructure.utils.SpecificationUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Objects;
import java.util.Optional;

@Service
public class CategoryMYSQLGateway implements CategoryGateway {
    private final CategoryRepository repository;

    public CategoryMYSQLGateway(CategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public Category create(final Category aCategory) {
        return this.repository.save(CategoryJpaEntity.from(aCategory)).toAggregate();
    }

    @Override
    public void deleteById(final CategoryID anId) {
        final String anIdValue = anId.getValue();
        if(this.repository.existsById(anIdValue)){
            this.repository.deleteById(anIdValue);
        }
    }

    @Override
    public Optional<Category> findById(final CategoryID anId) {
        return this.repository.findById(anId.getValue())
                .map(CategoryJpaEntity::toAggregate);
    }

    @Override
    public Category update(final Category aCategory) {
        return save(aCategory);
    }

    @Override
    public Pagination<Category> findAll(final CategorySearchQuery aQuery) {
        final var page = PageRequest.of(
                aQuery.page(),
                aQuery.perPage(),
                Sort.by(Sort.Direction.fromString(aQuery.direction()), aQuery.sort())
        );

        final var specifications = Optional.ofNullable(aQuery.terms())
                .filter(str -> !str.isBlank())
                .map(str -> SpecificationUtils.
                             <CategoryJpaEntity>like("name", str)
                             .or(SpecificationUtils.like("description",str))
                ).orElse(null);
        final var pageResult = this.repository.findAll(Specification.where(specifications),page);

        return new Pagination<>(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.map(CategoryJpaEntity::toAggregate).toList()
        );
    }

    public Category save(final Category aCategory) {
        return this.repository.save(CategoryJpaEntity.from(aCategory)).toAggregate();
    }
}
