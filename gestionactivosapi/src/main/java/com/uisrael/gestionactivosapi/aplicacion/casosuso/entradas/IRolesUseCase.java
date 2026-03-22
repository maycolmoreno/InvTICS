package com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas;

import java.util.List;

import com.uisrael.gestionactivosapi.dominio.entidades.Roles;

public interface IRolesUseCase {

	Roles crear(Roles rol);

	Roles obtenerPorId(int id);

	List<Roles> listar();

	Roles actualizar(Roles rol);

	void eliminar(int id);

}
