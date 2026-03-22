package com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas;

import java.util.List;

import com.uisrael.gestionactivosapi.dominio.entidades.Marcas;

public interface IMarcasUseCase {

	Marcas crear(Marcas Marcas);

	Marcas obtenerPorId(int id);

	List<Marcas> listar();

	Marcas actualizar(int id, Marcas marcas);

	void eliminar(int id);



}
