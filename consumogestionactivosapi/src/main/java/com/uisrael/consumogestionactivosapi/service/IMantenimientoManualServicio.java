package com.uisrael.consumogestionactivosapi.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.ImagenMantenimientoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.MantenimientoManualRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.MantenimientoManualResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.PaginaResponse;

public interface IMantenimientoManualServicio {

    MantenimientoManualResponseDTO crear(MantenimientoManualRequestDTO request);

    void guardarImagenes(Integer idMantenimiento, List<ImagenMantenimientoRequestDTO> imagenes);

    List<ImagenMantenimientoRequestDTO> subirImagenes(Integer idMantenimiento, List<MultipartFile> imagenes);

    List<MantenimientoManualResponseDTO> listarTodos();

    PaginaResponse<MantenimientoManualResponseDTO> listarTodosPaginado(int page, int size);

    MantenimientoManualResponseDTO obtenerDetalle(Integer id);

    List<MantenimientoManualResponseDTO> obtenerHistorial(Integer equipoId);

    MantenimientoManualResponseDTO cerrar(Integer id);

    byte[] descargarPdf(Integer id);

    void reenviarCorreo(Integer id);

    byte[] obtenerImagen(Integer idMantenimiento, String filename);
}
