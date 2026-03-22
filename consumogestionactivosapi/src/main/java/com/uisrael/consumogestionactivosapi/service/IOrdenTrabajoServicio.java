package com.uisrael.consumogestionactivosapi.service;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.OrdenCrearRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.OrdenGuardarRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.OrdenCrearResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.OrdenTrabajoResponseDTO;

public interface IOrdenTrabajoServicio {

    OrdenCrearResponseDTO crearOrden(OrdenCrearRequestDTO request);

    OrdenTrabajoResponseDTO obtenerOrden(Integer id);

    void guardarOrden(Integer id, OrdenGuardarRequestDTO request);
}
