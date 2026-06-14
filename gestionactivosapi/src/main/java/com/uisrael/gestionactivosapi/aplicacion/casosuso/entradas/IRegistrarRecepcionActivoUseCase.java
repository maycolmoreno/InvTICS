package com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.comandos.RegistrarRecepcionActivoCommand;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.RecepcionLoteJpa;

public interface IRegistrarRecepcionActivoUseCase {
    RecepcionLoteJpa ejecutar(RegistrarRecepcionActivoCommand command);
}
