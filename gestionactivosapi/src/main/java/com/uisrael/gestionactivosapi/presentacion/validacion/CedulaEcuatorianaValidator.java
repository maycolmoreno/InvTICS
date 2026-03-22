package com.uisrael.gestionactivosapi.presentacion.validacion;

import com.uisrael.gestionactivosapi.dominio.validacion.CedulaEcuatorianaUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CedulaEcuatorianaValidator implements ConstraintValidator<CedulaEcuatoriana, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        return CedulaEcuatorianaUtils.esValida(value);
    }
}
