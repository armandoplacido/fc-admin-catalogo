package com.armandoplacido.admin.catalogo.application.category.update;

import com.armandoplacido.admin.catalogo.application.UseCase;
import com.armandoplacido.admin.catalogo.domain.validation.handler.Notification;
import io.vavr.control.Either;

public abstract class UpdateCategoryUseCase extends UseCase<UpdateCategoryCommand, Either<Notification,UpdateCategoryOutput>> {

}
