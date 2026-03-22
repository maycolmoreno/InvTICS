package com.uisrael.consumogestionactivosapi.service;

import java.util.List;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.CustodiosRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.CustodiosResponseDTO;

public interface ICustodiosServicio {

    List<CustodiosResponseDTO> listarCustodios();

    CustodiosResponseDTO obtenerPorId(Integer idCustodio);

    void crearCustodio(CustodiosRequestDTO dto);

    void actualizarCustodio(Integer idCustodio, CustodiosRequestDTO dto);

    void actualizarEstado(Integer idCustodio, boolean estado);

	boolean existeCorreo(String correo);

	boolean existeCorreoParaOtro(String correo, int idCustodio);

	boolean existeCedula(String cedula);

	boolean existeCedulaParaOtro(String cedula, int idCustodio);
}
