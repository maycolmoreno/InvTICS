package com.uisrael.gestionactivosapi.infraestructura.servicios;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.uisrael.gestionactivosapi.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.ActividadPlanificadaJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.UsuariosJpa;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IActividadPlanificadaJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IUsuariosJpaRepositorio;
import com.uisrael.gestionactivosapi.presentacion.dto.request.ActividadPlanificadaRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.CambiarEstadoActividadRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.ActividadPlanificadaResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.MetricasCumplimientoResponseDTO;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IActividadPlanificadaUseCase;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ActividadPlanificadaService implements IActividadPlanificadaUseCase {

    private static final Set<String> TIPOS_VALIDOS = Set.of(
            "TAREA_DIARIA", "TAREA_SEMANAL", "MANTENIMIENTO_PROGRAMADO", "VISITA_TECNICA", "OBJETIVO_MENSUAL");
    private static final Set<String> PRIORIDADES_VALIDAS = Set.of("BAJA", "MEDIA", "ALTA", "URGENTE");
    private static final Set<String> ESTADOS_VALIDOS = Set.of(
            "PENDIENTE", "EN_PROGRESO", "COMPLETADA", "VENCIDA", "CANCELADA");

    private final IActividadPlanificadaJpaRepositorio actividadRepo;
    private final IUsuariosJpaRepositorio usuariosRepo;

    @Transactional
    public ActividadPlanificadaResponseDTO crear(ActividadPlanificadaRequestDTO request) {
        validarTipoActividad(request.getTipoActividad());
        validarPrioridad(request.getPrioridad());

        UsuariosJpa tecnico = usuariosRepo.findById(request.getTecnicoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Técnico no encontrado"));
        usuariosRepo.findById(request.getCreadoPorId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario creador no encontrado"));

        ActividadPlanificadaJpa entity = new ActividadPlanificadaJpa();
        entity.setTecnicoId(request.getTecnicoId());
        entity.setCreadoPorId(request.getCreadoPorId());
        entity.setTitulo(request.getTitulo());
        entity.setDescripcion(request.getDescripcion());
        entity.setTipoActividad(request.getTipoActividad().toUpperCase());
        entity.setPrioridad(request.getPrioridad() != null ? request.getPrioridad().toUpperCase() : "MEDIA");
        entity.setEstado("PENDIENTE");
        entity.setFechaInicio(request.getFechaInicio());
        entity.setFechaFin(request.getFechaFin());
        entity.setTiempoEstimadoMinutos(request.getTiempoEstimadoMinutos());
        entity.setReferenciaMantenimientoId(request.getReferenciaMantenimientoId());
        entity.setObservaciones(request.getObservaciones());

        return toDto(actividadRepo.save(entity));
    }

    @Transactional
    public ActividadPlanificadaResponseDTO actualizar(Long id, ActividadPlanificadaRequestDTO request) {
        ActividadPlanificadaJpa entity = actividadRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Actividad no encontrada"));

        if (request.getTipoActividad() != null) {
            validarTipoActividad(request.getTipoActividad());
            entity.setTipoActividad(request.getTipoActividad().toUpperCase());
        }
        if (request.getPrioridad() != null) {
            validarPrioridad(request.getPrioridad());
            entity.setPrioridad(request.getPrioridad().toUpperCase());
        }
        if (request.getTitulo() != null) entity.setTitulo(request.getTitulo());
        if (request.getDescripcion() != null) entity.setDescripcion(request.getDescripcion());
        if (request.getFechaInicio() != null) entity.setFechaInicio(request.getFechaInicio());
        if (request.getFechaFin() != null) entity.setFechaFin(request.getFechaFin());
        if (request.getTiempoEstimadoMinutos() != null) entity.setTiempoEstimadoMinutos(request.getTiempoEstimadoMinutos());
        if (request.getObservaciones() != null) entity.setObservaciones(request.getObservaciones());

        return toDto(actividadRepo.save(entity));
    }

    @Transactional
    public ActividadPlanificadaResponseDTO cambiarEstado(Long id, CambiarEstadoActividadRequestDTO request) {
        ActividadPlanificadaJpa entity = actividadRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Actividad no encontrada"));

        String nuevoEstado = request.getEstado().toUpperCase();
        validarEstado(nuevoEstado);

        entity.setEstado(nuevoEstado);
        if ("COMPLETADA".equals(nuevoEstado)) {
            entity.setFechaCompletada(LocalDateTime.now());
            if (request.getTiempoRealMinutos() != null) {
                entity.setTiempoRealMinutos(request.getTiempoRealMinutos());
            }
        }
        if (request.getObservaciones() != null) {
            entity.setObservaciones(request.getObservaciones());
        }

        return toDto(actividadRepo.save(entity));
    }

    public List<ActividadPlanificadaResponseDTO> listarTodas() {
        return actividadRepo.findAllByOrderByFechaInicioDesc().stream().map(this::toDto).toList();
    }

    public List<ActividadPlanificadaResponseDTO> listarPorTecnico(Integer tecnicoId) {
        return actividadRepo.findByTecnicoIdOrderByFechaInicioDesc(tecnicoId).stream().map(this::toDto).toList();
    }

    public List<ActividadPlanificadaResponseDTO> listarPorTecnicoYEstado(Integer tecnicoId, String estado) {
        return actividadRepo.findByTecnicoIdAndEstadoOrderByFechaInicioDesc(tecnicoId, estado.toUpperCase())
                .stream().map(this::toDto).toList();
    }

    public List<ActividadPlanificadaResponseDTO> listarPorTecnicoYRango(Integer tecnicoId, LocalDate desde, LocalDate hasta) {
        return actividadRepo.findByTecnicoIdAndFechaInicioBetweenOrderByFechaInicioAsc(tecnicoId, desde, hasta)
                .stream().map(this::toDto).toList();
    }

    public ActividadPlanificadaResponseDTO obtenerPorId(Long id) {
        return actividadRepo.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new RecursoNoEncontradoException("Actividad no encontrada"));
    }

    @Transactional
    public void marcarVencidas() {
        List<ActividadPlanificadaJpa> vencidas = actividadRepo.findVencidas(LocalDate.now());
        for (ActividadPlanificadaJpa a : vencidas) {
            a.setEstado("VENCIDA");
        }
        actividadRepo.saveAll(vencidas);
    }

    // ===================== MÉTRICAS =====================

    public MetricasCumplimientoResponseDTO obtenerMetricasTecnico(Integer tecnicoId, String periodo) {
        UsuariosJpa tecnico = usuariosRepo.findById(tecnicoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Técnico no encontrado"));

        LocalDate hoy = LocalDate.now();
        LocalDate desde;
        LocalDate hasta = hoy;

        switch (periodo != null ? periodo.toUpperCase() : "MENSUAL") {
            case "SEMANAL":
                desde = hoy.with(java.time.DayOfWeek.MONDAY);
                break;
            case "MENSUAL":
                desde = hoy.with(TemporalAdjusters.firstDayOfMonth());
                break;
            case "GLOBAL":
                desde = LocalDate.of(2000, 1, 1);
                break;
            default:
                desde = hoy.with(TemporalAdjusters.firstDayOfMonth());
                periodo = "MENSUAL";
        }

        long total = actividadRepo.countByTecnicoIdAndFechaInicioBetween(tecnicoId, desde, hasta);
        long completadas = actividadRepo.countByTecnicoIdAndEstadoAndFechaInicioBetween(tecnicoId, "COMPLETADA", desde, hasta);
        long pendientes = actividadRepo.countByTecnicoIdAndEstadoAndFechaInicioBetween(tecnicoId, "PENDIENTE", desde, hasta);
        long enProgreso = actividadRepo.countByTecnicoIdAndEstadoAndFechaInicioBetween(tecnicoId, "EN_PROGRESO", desde, hasta);
        long vencidas = actividadRepo.countVencidasEnPeriodo(tecnicoId, desde, hasta);
        long canceladas = actividadRepo.countByTecnicoIdAndEstadoAndFechaInicioBetween(tecnicoId, "CANCELADA", desde, hasta);
        long aTiempo = actividadRepo.countCompletadasATiempoEnPeriodo(tecnicoId, desde, hasta);
        double tiempoPromedio = actividadRepo.promedioTiempoEnPeriodo(tecnicoId, desde, hasta);

        double porcentajeCompletadas = total > 0 ? (completadas * 100.0 / total) : 0;
        double porcentajeCumplimiento = completadas > 0 ? (aTiempo * 100.0 / completadas) : 0;

        return MetricasCumplimientoResponseDTO.builder()
                .tecnicoId(tecnicoId)
                .tecnicoNombre(tecnico.getNombre())
                .periodo(periodo)
                .totalActividades(total)
                .completadas(completadas)
                .pendientes(pendientes)
                .enProgreso(enProgreso)
                .vencidas(vencidas)
                .canceladas(canceladas)
                .porcentajeCompletadas(Math.round(porcentajeCompletadas * 100.0) / 100.0)
                .porcentajeCumplimientoATiempo(Math.round(porcentajeCumplimiento * 100.0) / 100.0)
                .completadasATiempo(aTiempo)
                .completadasTarde(completadas - aTiempo)
                .tiempoPromedioMinutos(Math.round(tiempoPromedio * 100.0) / 100.0)
                .build();
    }

    public List<MetricasCumplimientoResponseDTO> obtenerMetricasGlobales(String periodo) {
        return usuariosRepo.findAll().stream()
                .filter(u -> u.getFkRol() != null &&
                        u.getFkRol().getNombre().toUpperCase().contains("TECNICO"))
                .map(tecnico -> obtenerMetricasTecnico(tecnico.getIdUsuario(), periodo))
                .toList();
    }

    // ===================== HELPERS =====================

    private void validarTipoActividad(String tipo) {
        if (tipo == null || !TIPOS_VALIDOS.contains(tipo.toUpperCase())) {
            throw new IllegalArgumentException(
                    "Tipo de actividad inválido. Valores permitidos: " + TIPOS_VALIDOS);
        }
    }

    private void validarPrioridad(String prioridad) {
        if (prioridad != null && !PRIORIDADES_VALIDAS.contains(prioridad.toUpperCase())) {
            throw new IllegalArgumentException(
                    "Prioridad inválida. Valores permitidos: " + PRIORIDADES_VALIDAS);
        }
    }

    private void validarEstado(String estado) {
        if (!ESTADOS_VALIDOS.contains(estado)) {
            throw new IllegalArgumentException(
                    "Estado inválido. Valores permitidos: " + ESTADOS_VALIDOS);
        }
    }

    private ActividadPlanificadaResponseDTO toDto(ActividadPlanificadaJpa entity) {
        return ActividadPlanificadaResponseDTO.builder()
                .idActividadPlanificada(entity.getIdActividadPlanificada())
                .tecnicoId(entity.getTecnicoId())
                .tecnicoNombre(entity.getFkTecnico() != null ? entity.getFkTecnico().getNombre() : null)
                .creadoPorId(entity.getCreadoPorId())
                .creadoPorNombre(entity.getFkCreadoPor() != null ? entity.getFkCreadoPor().getNombre() : null)
                .titulo(entity.getTitulo())
                .descripcion(entity.getDescripcion())
                .tipoActividad(entity.getTipoActividad())
                .prioridad(entity.getPrioridad())
                .estado(entity.getEstado())
                .fechaInicio(entity.getFechaInicio())
                .fechaFin(entity.getFechaFin())
                .fechaCompletada(entity.getFechaCompletada())
                .tiempoEstimadoMinutos(entity.getTiempoEstimadoMinutos())
                .tiempoRealMinutos(entity.getTiempoRealMinutos())
                .referenciaMantenimientoId(entity.getReferenciaMantenimientoId())
                .observaciones(entity.getObservaciones())
                .creadoEn(entity.getCreatedAt())
                .actualizadoEn(entity.getUpdatedAt())
                .build();
    }
}
