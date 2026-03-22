package com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas;

import com.uisrael.gestionactivosapi.presentacion.dto.response.OrdenTrabajoResponseDTO;

public interface IObtenerOrdenTrabajoUseCase {

    OrdenTrabajoResponseDTO obtener(Integer idMantenimiento);
}
