package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IMantenimientosUseCase;
import com.uisrael.gestionactivosapi.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.entidades.Mantenimientos;
import com.uisrael.gestionactivosapi.dominio.entidades.TipoOrigenMantenimiento;
import com.uisrael.gestionactivosapi.dominio.repositorios.IMantenimientosRepositorio;

public class MantenimientosUseCaseImpl implements IMantenimientosUseCase {

    private final IMantenimientosRepositorio repositorio;

    public MantenimientosUseCaseImpl(IMantenimientosRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    @Transactional
    public Mantenimientos crear(Mantenimientos mantenimiento) {
        if (mantenimiento.getEquipoId() == null) {
            throw new IllegalArgumentException("equipoId es obligatorio");
        }
        if (mantenimiento.getFechaProgramada() == null) {
            throw new IllegalArgumentException("fechaProgramada es obligatoria");
        }
        if (mantenimiento.getDescripcion() == null || mantenimiento.getDescripcion().isBlank()) {
            throw new IllegalArgumentException("descripcion es obligatoria");
        }
        if (mantenimiento.getTipoOrigen() == null) {
            mantenimiento.setTipoOrigen(TipoOrigenMantenimiento.MANUAL);
        }
        if (mantenimiento.getTipoOrigen() == TipoOrigenMantenimiento.ODOO_HELPDESK
                && (mantenimiento.getOdooTicketId() == null || mantenimiento.getOdooTicketId().isBlank())) {
            throw new IllegalArgumentException("odooTicketId es obligatorio para mantenimientos de Odoo");
        }
        if (mantenimiento.getEstado() != null) {
            mantenimiento.setEstado(mantenimiento.getEstado().trim().toUpperCase());
        }
        if (mantenimiento.getCreadoEn() == null) {
            mantenimiento.setCreadoEn(LocalDateTime.now());
        }
        return repositorio.guardar(mantenimiento);
    }

    @Override
    public List<Mantenimientos> listar() {
        return repositorio.listarTodos();
    }

    @Override
    public Mantenimientos obtenerPorId(int id) {
        return repositorio.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Mantenimiento no encontrado"));
    }
}
