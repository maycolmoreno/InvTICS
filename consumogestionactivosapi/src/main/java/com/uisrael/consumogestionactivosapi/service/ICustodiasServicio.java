package com.uisrael.consumogestionactivosapi.service;

import java.util.List;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.CustodiasRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.CustodiasResponseDTO;

public interface ICustodiasServicio {

    List<CustodiasResponseDTO> listarCustodias();

    void crearCustodia(CustodiasRequestDTO dto); // (si quieres mantener 1 equipo)

    // ✅ NUEVO: crea acta con varios equipos
    List<CustodiasResponseDTO> crearCustodiaActa(CustodiasRequestDTO dto);

    CustodiasResponseDTO obtenerPorId(Integer id);

    void actualizarCustodia(Integer id, CustodiasRequestDTO dto);

    void actualizarEstado(Integer id, boolean estado);
}
