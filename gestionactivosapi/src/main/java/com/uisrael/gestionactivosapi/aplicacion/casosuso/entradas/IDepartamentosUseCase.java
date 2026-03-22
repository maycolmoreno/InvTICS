package com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas;

import java.util.List;

import com.uisrael.gestionactivosapi.dominio.entidades.Departamentos;

public interface IDepartamentosUseCase {

	Departamentos crear (Departamentos departamento);

	Departamentos obtenerPorId(int id);

	List<Departamentos> listar();

	Departamentos actualizar(int id, Departamentos departamento);

	Departamentos actualizarEstado(int id, boolean estado);

	boolean nombreExiste(String nombre);

    boolean nombreExisteParaOtro(String nombre, Integer idDepartamento);

}
