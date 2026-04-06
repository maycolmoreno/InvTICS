package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.ICrearMantenimientosUseCase;
import com.uisrael.gestionactivosapi.dominio.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.entidades.Custodias;
import com.uisrael.gestionactivosapi.dominio.entidades.EstadoInternoMantenimiento;
import com.uisrael.gestionactivosapi.dominio.entidades.Mantenimientos;
import com.uisrael.gestionactivosapi.dominio.entidades.PrioridadMantenimiento;
import com.uisrael.gestionactivosapi.dominio.entidades.TipoMantenimiento;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.MantenimientoRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.CustodiasRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.EquipoRepositorioPuerto;

public class CrearMantenimientosUseCaseImpl implements ICrearMantenimientosUseCase {

    private static final long DIAS_MINIMO_PREVENTIVO = 90L;

    private final MantenimientoRepositorioPuerto mantenimientoRepositorio;
    private final CustodiasRepositorioPuerto custodiasRepositorio;
    private final EquipoRepositorioPuerto equiposRepositorio;

    public CrearMantenimientosUseCaseImpl(MantenimientoRepositorioPuerto mantenimientoRepositorio,
            CustodiasRepositorioPuerto custodiasRepositorio,
            EquipoRepositorioPuerto equiposRepositorio) {
        this.mantenimientoRepositorio = mantenimientoRepositorio;
        this.custodiasRepositorio = custodiasRepositorio;
        this.equiposRepositorio = equiposRepositorio;
    }

    @Override
    public Integer crear(List<Integer> equiposIds, String tipoMantenimiento, String prioridad,
            Integer idUsuarioTecnico) {

        if (equiposIds == null || equiposIds.isEmpty()) {
            throw new IllegalArgumentException("equiposIds es obligatorio");
        }

        String tipoVal = (tipoMantenimiento == null || tipoMantenimiento.isBlank()) ? "PREVENTIVO" : tipoMantenimiento;
        String prioVal = (prioridad == null || prioridad.isBlank()) ? "NORMAL" : prioridad;
        TipoMantenimiento tipo = TipoMantenimiento.valueOf(tipoVal.toUpperCase());
        PrioridadMantenimiento prio = PrioridadMantenimiento.valueOf(prioVal.toUpperCase());

        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear();
        Integer max = mantenimientoRepositorio.obtenerMaxSecuenciaPorYear(year);
        int secuencia = (max == null) ? 1 : (max + 1);

        List<Mantenimientos> creados = new ArrayList<>();

        for (Integer equipoId : equiposIds) {
            if (equipoId == null) continue;

            equiposRepositorio.buscarPorId(equipoId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Equipo no encontrado: " + equipoId));

            Custodias custodia = custodiasRepositorio.buscarActivaPorEquipo(equipoId)
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "No existe custodio activo para el equipo " + equipoId));
            if (custodia.getFkCustodio() == null || custodia.getFkCustodio().getIdCustodio() <= 0) {
                throw new IllegalStateException("La custodia activa del equipo " + equipoId + " no tiene custodio válido");
            }

            if (tipo == TipoMantenimiento.PREVENTIVO) {
                validarVentanaPreventiva(equipoId, now.toLocalDate());
            }

            Mantenimientos m = new Mantenimientos();
            m.setEquipoId(equipoId);
            m.setIdCliente(custodia.getFkCustodio().getIdCustodio());
            m.setFechaProgramada(now);
            m.setDescripcion("Orden de trabajo creada");
            m.setTipoMantenimiento(tipo.name());
            m.setEstado(prio.name());
            m.setEstadoInterno(EstadoInternoMantenimiento.EN_PROCESO);
            m.setCreadoEn(now);
            m.setYearSnapshoted(year);
            m.setSineSnapshoted(String.format("%d-%04d", year, secuencia++));
            if (idUsuarioTecnico != null) {
                m.setIdUsuario(idUsuarioTecnico);
            }

            Mantenimientos guardado = mantenimientoRepositorio.guardar(m);
            creados.add(guardado);
        }

        if (creados.isEmpty()) {
            throw new IllegalStateException("No se pudo crear ningun mantenimiento");
        }

        return creados.get(0).getIdMantenimiento();
    }

    private void validarVentanaPreventiva(Integer equipoId, LocalDate fechaActual) {
        LocalDateTime ultimoCierre = mantenimientoRepositorio.obtenerUltimoCierrePorEquipo(equipoId);
        if (ultimoCierre == null) {
            return;
        }

        LocalDate fechaUltimo = ultimoCierre.toLocalDate();
        long diasTranscurridos = ChronoUnit.DAYS.between(fechaUltimo, fechaActual);
        if (diasTranscurridos >= DIAS_MINIMO_PREVENTIVO) {
            return;
        }

        long diasFaltantes = DIAS_MINIMO_PREVENTIVO - diasTranscurridos;
        LocalDate fechaHabil = fechaUltimo.plusDays(DIAS_MINIMO_PREVENTIVO);
        throw new IllegalArgumentException(
                "El equipo " + equipoId + " aun no cumple la ventana para mantenimiento preventivo. "
                        + "Faltan " + diasFaltantes + " dias (habil desde " + fechaHabil + ").");
    }
}
