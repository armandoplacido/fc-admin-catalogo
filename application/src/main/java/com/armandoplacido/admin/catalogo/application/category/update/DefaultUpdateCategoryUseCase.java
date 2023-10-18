package com.armandoplacido.admin.catalogo.application.category.update;

import com.armandoplacido.admin.catalogo.domain.category.Category;
import com.armandoplacido.admin.catalogo.domain.category.CategoryGateway;
import com.armandoplacido.admin.catalogo.domain.category.CategoryID;
import com.armandoplacido.admin.catalogo.domain.exceptions.DomainException;
import com.armandoplacido.admin.catalogo.domain.validation.Error;
import com.armandoplacido.admin.catalogo.domain.validation.handler.Notification;
import io.vavr.API;
import io.vavr.control.Either;

import java.util.Objects;
import java.util.function.Supplier;

import static io.vavr.control.Either.left;

public class DefaultUpdateCategoryUseCase extends UpdateCategoryUseCase{

    private final CategoryGateway categoryGateway;

    public DefaultUpdateCategoryUseCase(final CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public Either<Notification, UpdateCategoryOutput> execute(UpdateCategoryCommand aCommand) {
        final CategoryID anId = CategoryID.from(aCommand.id());
        final String aName = aCommand.name();
        final String aDescription = aCommand.description();
        final boolean isActive = aCommand.isActive();

        final var aCategory = this.categoryGateway.findById(anId).orElseThrow(notFound(anId));

        final var notification = Notification.create();

        aCategory.update(
                aName,
                aDescription,
                isActive
        ).validate(notification);

        return notification.hasError() ? left(notification) : update(aCategory);
    }

    private Either<Notification, UpdateCategoryOutput> update(final Category aCategory) {
        return API.Try(() -> this.categoryGateway.update(aCategory))
                .toEither()
                .bimap(Notification::create,UpdateCategoryOutput::from);
    }

    private static Supplier<DomainException> notFound(CategoryID anId) {
        return () -> DomainException.with(new Error("Category with ID %s was not found".formatted(anId.getValue())));
    }
}
