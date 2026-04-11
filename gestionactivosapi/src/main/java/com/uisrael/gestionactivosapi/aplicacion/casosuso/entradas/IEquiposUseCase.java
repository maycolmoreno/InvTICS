package com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas;

import java.util.List;

import com.uisrael.gestionactivosapi.dominio.entidades.Equipos;
import com.uisrael.gestionactivosapi.dominio.modelo.Pagina;

public interface IEquiposUseCase {
    Equipos crear(Equipos equipo);
    List<Equipos> listar();
    Pagina<Equipos> listarPaginado(int pagina, int tamanio);
    Equipos obtenerPorId(int id);

    Equipos actualizar(int id, Equipos equipo);
    Equipos actualizarEstado(int id, boolean estado);

	boolean existeCodigo(String codigo);

	boolean existeCodigoParaOtro(String codigo, int idEquipo);

	boolean existeSerial(String serial);

	boolean existeSerialParaOtro(String serial, int idEquipo);

	boolean existeMAC(String mac);

	boolean existeMACParaOtro(String mac, int idEquipo);
}
