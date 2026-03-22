package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.ICustodiasUseCase;
import com.uisrael.gestionactivosapi.aplicacion.excepciones.DuplicidadException;
import com.uisrael.gestionactivosapi.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.entidades.Custodias;
import com.uisrael.gestionactivosapi.dominio.repositorios.ICustodiasRepositorio;

public class CustodiasUseCaseImpl implements ICustodiasUseCase {

    private final ICustodiasRepositorio repositorio;

    public CustodiasUseCaseImpl(ICustodiasRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    @Transactional
    public Custodias crear(Custodias custodia) {
        if (custodia == null) {
            throw new IllegalArgumentException("La custodia es obligatoria");
        }
        validarEquipoDisponibleParaCustodia(custodia);
        return repositorio.guardar(custodia);
    }

    @Override
    public Custodias obtenerPorId(int id) {
        return repositorio.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Custodia no encontrada"));
    }

    @Override
    public List<Custodias> listar() {
        return repositorio.listarTodos();
    }

    @Override
    @Transactional
    public Custodias actualizar(int id, Custodias custodia) {
        if (custodia == null) {
            throw new IllegalArgumentException("La custodia es obligatoria");
        }
        repositorio.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Custodia no encontrada"));

        if (custodia.getFkEquipo() == null || custodia.getFkEquipo().getIdEquipo() <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un equipo");
        }

        if (custodia.isEstado()
                && repositorio.existeCustodiaActivaPorEquipoParaOtroRegistro(custodia.getFkEquipo().getIdEquipo(), id)) {
            throw new DuplicidadException("El equipo ya se encuentra asociado a otro custodio activo");
        }

        Custodias actualizada = repositorio.actualizar(id, custodia);
        return actualizada;
    }

    @Override
    @Transactional
    public Custodias actualizarEstado(int id, Custodias custodia) {
        if (custodia == null) {
            throw new IllegalArgumentException("La custodia es obligatoria");
        }
        repositorio.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Custodia no encontrada"));
        return repositorio.actualizarEstado(id, custodia);
    }

    @Override
    public long contarPorTipoMovimiento(String tipoMovimiento) {
        return repositorio.contarPorTipoMovimiento(tipoMovimiento);
    }

    private void validarEquipoDisponibleParaCustodia(Custodias custodia) {
        if (custodia.getFkEquipo() == null || custodia.getFkEquipo().getIdEquipo() <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un equipo");
        }

        if (repositorio.existeCustodiaActivaPorEquipo(custodia.getFkEquipo().getIdEquipo())) {
            throw new DuplicidadException("El equipo ya se encuentra asociado a un custodio activo");
        }
    }
}
