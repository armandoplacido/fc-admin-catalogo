package com.armandoplacido.admin.catalogo.application.category.create;

import com.armandoplacido.admin.catalogo.application.UseCase;
import com.armandoplacido.admin.catalogo.domain.validation.handler.Notification;
import io.vavr.control.Either;

public abstract class CreateCategoryUseCase
        extends UseCase<CreateCategoryCommand, Either<Notification,CreateCategoryOutput>>{

}
