package com.armandoplacido.admin.catalogo.domain.validation;

import java.util.List;

public interface  ValidationHandler {
     ValidationHandler append(final Error anError);

    ValidationHandler append(final ValidationHandler anHandler);

    ValidationHandler validate(final Validation aValidation);

    List<Error> getErrors();

    default boolean hasError() {
        return getErrors() != null && !getErrors().isEmpty();
    }

    default Error firstError() {
        if(getErrors() != null && !getErrors().isEmpty()){
            return getErrors().get(0);
        }
        return null;
    }


    public interface Validation {
        void validate();
    }
}
