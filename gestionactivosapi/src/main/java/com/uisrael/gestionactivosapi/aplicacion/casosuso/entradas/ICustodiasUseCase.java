package com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas;

import java.util.List;

import com.uisrael.gestionactivosapi.dominio.entidades.Custodias;
import com.uisrael.gestionactivosapi.dominio.modelo.Pagina;

public interface ICustodiasUseCase {

    Custodias crear(Custodias custodia);

    Custodias obtenerPorId(int id);

    List<Custodias> listar();

    Pagina<Custodias> listarPaginado(int pagina, int tamanio);

    Custodias actualizar(int id, Custodias custodia);

    Custodias actualizarEstado(int id, Custodias custodia);

    long contarPorTipoMovimiento(String tipoMovimiento);
}
