package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.ICustodiasUseCase;
import com.uisrael.gestionactivosapi.aplicacion.excepciones.DuplicidadException;
import com.uisrael.gestionactivosapi.dominio.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.entidades.Custodias;
import com.uisrael.gestionactivosapi.dominio.modelo.Pagina;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.CustodiasRepositorioPuerto;

public class CustodiasUseCaseImpl implements ICustodiasUseCase {

    private final CustodiasRepositorioPuerto custodiasRepositorio;

    public CustodiasUseCaseImpl(CustodiasRepositorioPuerto custodiasRepositorio) {
        this.custodiasRepositorio = custodiasRepositorio;
    }

    @Override
    @Transactional
    public Custodias crear(Custodias custodia) {
        if (custodia == null) {
            throw new IllegalArgumentException("La custodia es obligatoria");
        }
        validarEquipoDisponibleParaCustodia(custodia);
        return custodiasRepositorio.guardar(custodia);
    }

    @Override
    public Custodias obtenerPorId(int id) {
        return custodiasRepositorio.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Custodia no encontrada"));
    }

    @Override
    public List<Custodias> listar() {
        return custodiasRepositorio.listarTodos();
    }

    @Override
    public Pagina<Custodias> listarPaginado(int pagina, int tamanio) {
        return custodiasRepositorio.listarPaginado(pagina, tamanio);
    }

    @Override
    @Transactional
    public Custodias actualizar(int id, Custodias custodia) {
        if (custodia == null) {
            throw new IllegalArgumentException("La custodia es obligatoria");
        }
        custodiasRepositorio.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Custodia no encontrada"));

        if (custodia.getFkEquipo() == null || custodia.getFkEquipo().getIdEquipo() <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un equipo");
        }

        if (custodia.isEstado()
                && custodiasRepositorio.existeCustodiaActivaPorEquipoParaOtroRegistro(custodia.getFkEquipo().getIdEquipo(), id)) {
            throw new DuplicidadException("El equipo ya se encuentra asociado a otro custodio activo");
        }

        Custodias actualizada = custodiasRepositorio.actualizar(id, custodia);
        return actualizada;
    }

    @Override
    @Transactional
    public Custodias actualizarEstado(int id, Custodias custodia) {
        if (custodia == null) {
            throw new IllegalArgumentException("La custodia es obligatoria");
        }
        custodiasRepositorio.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Custodia no encontrada"));
        return custodiasRepositorio.actualizarEstado(id, custodia);
    }

    @Override
    public long contarPorTipoMovimiento(String tipoMovimiento) {
        return custodiasRepositorio.contarPorTipoMovimiento(tipoMovimiento);
    }

    private void validarEquipoDisponibleParaCustodia(Custodias custodia) {
        if (custodia.getFkEquipo() == null || custodia.getFkEquipo().getIdEquipo() <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un equipo");
        }

        if (custodiasRepositorio.existeCustodiaActivaPorEquipo(custodia.getFkEquipo().getIdEquipo())) {
            throw new DuplicidadException("El equipo ya se encuentra asociado a un custodio activo");
        }
    }
}
