package com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas;

import java.util.List;

import com.uisrael.gestionactivosapi.dominio.modelo.Pagina;

import com.uisrael.gestionactivosapi.infraestructura.servicios.modelo.ImagenMantenimientoComando;
import com.uisrael.gestionactivosapi.infraestructura.servicios.modelo.MantenimientoManualComando;
import com.uisrael.gestionactivosapi.presentacion.dto.response.MantenimientoManualResponseDTO;

public interface IMantenimientoManualUseCase {

    MantenimientoManualResponseDTO crear(MantenimientoManualComando request, String correoAutenticado);

    void guardarImagenes(Integer idMantenimiento, List<ImagenMantenimientoComando> imagenes);

    List<MantenimientoManualResponseDTO> listarTodos();

    Pagina<MantenimientoManualResponseDTO> listarTodosPaginado(int pagina, int tamanio);

    List<MantenimientoManualResponseDTO> obtenerHistorial(Integer equipoId);

    List<MantenimientoManualResponseDTO> listarPorTecnico(Integer tecnicoId);

    Pagina<MantenimientoManualResponseDTO> listarPorTecnicoPaginado(Integer tecnicoId, int pagina, int tamanio);

    MantenimientoManualResponseDTO obtenerDetalle(Integer idMantenimiento);

    MantenimientoManualResponseDTO cerrar(Integer idMantenimiento, String descripcionTrabajoRealizado,
            String resultadoTecnico, String cerradoPor);
}
