package com.armandoplacido.admin.catalogo.application.category.create;

import com.armandoplacido.admin.catalogo.domain.category.Category;
import com.armandoplacido.admin.catalogo.domain.category.CategoryGateway;
import com.armandoplacido.admin.catalogo.domain.validation.handler.Notification;
import io.vavr.API;
import io.vavr.control.Either;
import java.util.Objects;

import static io.vavr.API.Try;

public class DefaultCreateCategoryUseCase extends CreateCategoryUseCase{
    private final CategoryGateway categoryGateway;

    public DefaultCreateCategoryUseCase(final CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public Either<Notification,CreateCategoryOutput> execute(final CreateCategoryCommand aCommand) {

        final var notification = Notification.create();

        final var aCategory = Category.newCategory(
                aCommand.name(),
                aCommand.description(),
                aCommand.isActive()
        );
        aCategory.validate(notification);

        return notification.hasError() ? API.Left(notification) : create(aCategory);
    }

    public Either<Notification,CreateCategoryOutput> create(final Category aCategory){
        return Try(() -> this.categoryGateway.create(aCategory))
                .toEither()
                .bimap(Notification::create,CreateCategoryOutput::from);
    }

}
