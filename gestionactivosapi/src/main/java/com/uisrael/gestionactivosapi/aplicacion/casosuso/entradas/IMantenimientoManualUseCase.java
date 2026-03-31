package com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas;

import java.util.List;

import com.uisrael.gestionactivosapi.infraestructura.servicios.modelo.ImagenMantenimientoComando;
import com.uisrael.gestionactivosapi.infraestructura.servicios.modelo.MantenimientoManualComando;
import com.uisrael.gestionactivosapi.presentacion.dto.response.MantenimientoManualResponseDTO;

public interface IMantenimientoManualUseCase {

    MantenimientoManualResponseDTO crear(MantenimientoManualComando request, String correoAutenticado);

    void guardarImagenes(Integer idMantenimiento, List<ImagenMantenimientoComando> imagenes);

    List<MantenimientoManualResponseDTO> listarTodos();

    List<MantenimientoManualResponseDTO> obtenerHistorial(Integer equipoId);

    MantenimientoManualResponseDTO obtenerDetalle(Integer idMantenimiento);

    MantenimientoManualResponseDTO cerrar(Integer idMantenimiento, String descripcionTrabajoRealizado);
}
