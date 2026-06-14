package com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.comandos.RegistrarRecepcionStockCommand;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.RecepcionLoteJpa;

public interface IRegistrarRecepcionStockUseCase {
    RecepcionLoteJpa ejecutar(RegistrarRecepcionStockCommand command);
}
