package com.uisrael.gestionactivosapi.presentacion.validacion;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = CedulaEcuatorianaValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface CedulaEcuatoriana {

    String message() default "La cédula debe ser ecuatoriana válida de 10 dígitos";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
