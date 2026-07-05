package com.uisrael.consumogestionactivosapi.service;

import java.util.List;
import java.util.Optional;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.CustodiosRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.CustodiosResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.sync.CandidatoDirectorioDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.sync.CustodioResueltoDTO;

public interface ICustodiosServicio {

    List<CustodiosResponseDTO> listarCustodios();

    List<CandidatoDirectorioDTO> buscarEnDirectorio(String q);

    CustodioResueltoDTO resolverDesdeDirectorio(String cedula);

    Optional<CandidatoDirectorioDTO> previsualizarDesdeDirectorio(String cedula);

    CustodiosResponseDTO obtenerPorId(Integer idCustodio);

    void crearCustodio(CustodiosRequestDTO dto);

    void actualizarCustodio(Integer idCustodio, CustodiosRequestDTO dto);

    void actualizarEstado(Integer idCustodio, boolean estado);

	boolean existeCorreo(String correo);

	boolean existeCorreoParaOtro(String correo, int idCustodio);

	boolean existeCedula(String cedula);

	boolean existeCedulaParaOtro(String cedula, int idCustodio);
}
