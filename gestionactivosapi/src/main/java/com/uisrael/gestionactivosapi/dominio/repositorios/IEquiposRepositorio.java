package com.uisrael.gestionactivosapi.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.uisrael.gestionactivosapi.dominio.entidades.Equipos;

public interface IEquiposRepositorio {

    Equipos guardar(Equipos equipo);

    Optional<Equipos> buscarPorId(int id);

    List<Equipos> listarTodos();

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
