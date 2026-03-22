package com.uisrael.consumogestionactivosapi.service;

import java.util.List;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.EquiposRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.EquiposResponseDTO;

public interface IEquiposServicio {

    List<EquiposResponseDTO> listarEquipos();

    void crearEquipo(EquiposRequestDTO dto);

    EquiposResponseDTO obtenerPorId(Integer idEquipo);

    void actualizarEquipo(Integer idEquipo, EquiposRequestDTO dto);

    void actualizarEstado(Integer idEquipo, boolean estado);

	boolean existeCodigo(String codigo);

	boolean existeCodigoParaOtro(String codigo, int idEquipo);

	boolean existeSerial(String serial);

	boolean existeSerialParaOtro(String serial, int idEquipo);

	boolean existeIP(String ip);

	boolean existeIPParaOtro(String ip, int idEquipo);

	boolean existeMAC(String mac);

	boolean existeMACParaOtro(String mac, int idEquipo);
}
