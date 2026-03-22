package com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas;

import java.util.List;

import com.uisrael.gestionactivosapi.dominio.entidades.Cargos;

public interface ICargosUseCase {

	Cargos crear (Cargos cargo);

	Cargos obtenerPorId(int id);

	List<Cargos> listar();

	Cargos actualizar(int id, Cargos cargo);

	Cargos actualizarEstado(int id, boolean estado);

	boolean nombreExiste(String nombre);

    boolean nombreExisteParaOtro(String nombre, Integer idCargo);

}
