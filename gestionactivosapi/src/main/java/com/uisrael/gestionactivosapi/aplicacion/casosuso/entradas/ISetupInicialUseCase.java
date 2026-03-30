package com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.comandos.CrearAdminSetupCommand;

public interface ISetupInicialUseCase {

	boolean esNecesario();

	void crearAdmin(CrearAdminSetupCommand command);
}
