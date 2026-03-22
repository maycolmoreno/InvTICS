package com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas;

import java.util.List;

import com.uisrael.gestionactivosapi.dominio.entidades.Equipos;

public interface IEquiposUseCase {
    Equipos crear(Equipos equipo);
    List<Equipos> listar();
    Equipos obtenerPorId(int id);

    Equipos actualizar(int id, Equipos equipo);
    Equipos actualizarEstado(int id, boolean estado);

	boolean existeCodigo(String codigo);

	boolean existeCodigoParaOtro(String codigo, int idEquipo);

	boolean existeSerial(String serial);

	boolean existeSerialParaOtro(String serial, int idEquipo);

	boolean existeIP(String ip);

	boolean existeIPParaOtro(String ip, int idEquipo);

	boolean existeMAC(String mac);

	boolean existeMACParaOtro(String mac, int idEquipo);
}
