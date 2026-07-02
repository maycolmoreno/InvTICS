package com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas;

import java.util.List;

import com.uisrael.gestionactivosapi.dominio.entidades.Custodias;

public interface ICustodiasUseCase {

    Custodias crear(Custodias custodia);

    Custodias obtenerPorId(int id);

    List<Custodias> listar();

    Custodias actualizar(int id, Custodias custodia);

    Custodias actualizarEstado(int id, Custodias custodia);

    void registrarActaPdf(List<Integer> ids, String rutaPdf);

    void registrarActaFirmada(int id, String rutaActaFirmada);
}
