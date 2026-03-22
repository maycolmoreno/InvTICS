package com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas;

import java.util.List;

public interface ICrearMantenimientosUseCase {

    Integer crear(List<Integer> equiposIds, String tipoMantenimiento, String prioridad, Integer idUsuarioTecnico);
}
