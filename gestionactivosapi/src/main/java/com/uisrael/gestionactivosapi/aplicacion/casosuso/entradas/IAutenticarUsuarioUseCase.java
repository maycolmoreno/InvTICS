package com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas;

import com.uisrael.gestionactivosapi.dominio.entidades.Usuarios;
import java.util.Optional;

public interface IAutenticarUsuarioUseCase {
	Optional<Usuarios> ejecutar(String correo, String contrasena);
}
