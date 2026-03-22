package com.uisrael.gestionactivosapi.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.uisrael.gestionactivosapi.dominio.entidades.Custodias;

public interface ICustodiasRepositorio {

    Custodias guardar(Custodias custodia);

    Optional<Custodias> buscarPorId(int id);

    List<Custodias> listarTodos();

    Custodias actualizar(int id, Custodias custodia);

    Custodias actualizarEstado(int id, Custodias custodia);

    boolean existeCustodiaActivaPorEquipo(int idEquipo);

    boolean existeCustodiaActivaPorEquipoParaOtroRegistro(int idEquipo, int idCustodiaEquipo);

    long contarPorTipoMovimiento(String tipoMovimiento);

    java.util.Optional<Custodias> buscarActivaPorEquipo(int idEquipo);
}
