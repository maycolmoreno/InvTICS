package com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas;

import com.uisrael.gestionactivosapi.dominio.entidades.Activo;
import java.util.Optional;

public interface IBuscarActivoPorIdUseCase {
	Optional<Activo> ejecutar(int idActivo);
}
