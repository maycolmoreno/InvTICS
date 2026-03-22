package com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas;

import java.util.List;

import com.uisrael.gestionactivosapi.dominio.entidades.Ubicaciones;

public interface IUbicacionesUseCase {

	Ubicaciones crear (Ubicaciones ubicacion);

	Ubicaciones obtenerPorId (int id);

	List<Ubicaciones> listar();

	Ubicaciones actualizar(int id, Ubicaciones ubicacion);

	Ubicaciones actualizarEstado(int id, boolean estado);

	boolean nombreExiste(String nombre);

    boolean nombreExisteParaOtro(String nombre, Integer idUbicacion);

}
