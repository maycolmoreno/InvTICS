package com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas;

import java.util.List;

import com.uisrael.gestionactivosapi.dominio.entidades.Usuarios;

public interface IUsuariosUseCase {

	Usuarios crear(Usuarios usuario);

	Usuarios obtenerPorId(int id);

	List<Usuarios> listar();

	void eliminar(int id);

	Usuarios actualizar(Usuarios usuario);

}
