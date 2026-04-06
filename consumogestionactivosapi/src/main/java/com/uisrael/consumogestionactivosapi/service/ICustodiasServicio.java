package com.uisrael.consumogestionactivosapi.service;

import java.util.List;
import java.util.Map;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.CustodiasRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.ActaResumenDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.CustodiasResponseDTO;

public interface ICustodiasServicio {

    List<CustodiasResponseDTO> listarCustodias();

    void crearCustodia(CustodiasRequestDTO dto); // (si quieres mantener 1 equipo)

    // ✅ NUEVO: crea acta con varios equipos
    List<CustodiasResponseDTO> crearCustodiaActa(CustodiasRequestDTO dto);

    CustodiasResponseDTO obtenerPorId(Integer id);

    void actualizarCustodia(Integer id, CustodiasRequestDTO dto);

    void actualizarEstado(Integer id, boolean estado);

    /**
     * Agrupa las custodias por acta (idCustodio + tipo + fechaInicio) y devuelve
     * la lista ordenada de actas con su resumen y el mapa de detalles por key.
     */
    record ActasAgrupadas(List<ActaResumenDTO> actas, Map<String, List<CustodiasResponseDTO>> detalles) {}

    ActasAgrupadas agruparPorActa();
}
