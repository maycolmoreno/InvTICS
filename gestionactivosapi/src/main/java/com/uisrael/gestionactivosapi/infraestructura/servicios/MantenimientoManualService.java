package com.uisrael.gestionactivosapi.infraestructura.servicios;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import com.uisrael.gestionactivosapi.dominio.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.entidades.EstadoInternoMantenimiento;
import com.uisrael.gestionactivosapi.dominio.entidades.FirmaMantenimiento;
import com.uisrael.gestionactivosapi.dominio.entidades.ResultadoTecnico;
import com.uisrael.gestionactivosapi.dominio.modelo.Pagina;
import com.uisrael.gestionactivosapi.dominio.entidades.TipoFirma;
import com.uisrael.gestionactivosapi.dominio.entidades.TipoOrigenMantenimiento;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.FirmaMantenimientoRepositorioPuerto;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.ActividadChecklistJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.ActividadRealizadaJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CustodiosJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.EquiposJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.ImagenMantenimientoJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.MantenimientoEquipoJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.MantenimientosJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.UsuariosJpa;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IActividadChecklistJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IActividadRealizadaJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.ICustodiosJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IEquiposJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IImagenMantenimientoJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IMantenimientoEquipoJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IMantenimientosJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IUsuariosJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.servicios.modelo.ActividadManualComando;
import com.uisrael.gestionactivosapi.infraestructura.servicios.modelo.ImagenMantenimientoComando;
import com.uisrael.gestionactivosapi.infraestructura.servicios.modelo.MantenimientoManualComando;
import com.uisrael.gestionactivosapi.presentacion.dto.response.ActividadManualResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.EquipoEnMantenimientoDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.ImagenMantenimientoResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.MantenimientoManualResponseDTO;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IMantenimientoManualUseCase;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MantenimientoManualService implements IMantenimientoManualUseCase {

    private static final String TRABAJO_REALIZADO_LABEL = "Trabajo realizado:";

    private final IMantenimientosJpaRepositorio mantenimientosRepo;
    private final IMantenimientoEquipoJpaRepositorio mantenimientoEquipoRepo;
    private final IActividadRealizadaJpaRepositorio actividadRealizadaRepo;
    private final IActividadChecklistJpaRepositorio actividadChecklistRepo;
    private final IImagenMantenimientoJpaRepositorio imagenRepo;
    private final IEquiposJpaRepositorio equiposRepo;
    private final ICustodiosJpaRepositorio custodiosRepo;
    private final IUsuariosJpaRepositorio usuariosRepo;
    private final MantenimientoProgramadoService programadoService;
    private final NotificacionService notificacionService;
    private final FirmaMantenimientoRepositorioPuerto firmaMantenimientoRepositorio;

    @Transactional
    public MantenimientoManualResponseDTO crear(MantenimientoManualComando request, String correoAutenticado) {
        List<Integer> equipoIds = request.equipoIds();
        if (equipoIds == null || equipoIds.isEmpty()) {
            throw new IllegalArgumentException("Debe especificar al menos un equipo");
        }

        List<EquiposJpa> equiposValidados = new ArrayList<>();
        for (Integer eqId : equipoIds) {
            EquiposJpa equipo = equiposRepo.findById(eqId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Equipo no encontrado: " + eqId));
            if (!equipo.isEstado()) {
                throw new IllegalArgumentException("El equipo " + eqId + " no esta activo");
            }
            if (mantenimientosRepo.existsByEquipoEnProcesoIncluyendoMultiple(
                    equipo.getIdEquipo(), EstadoInternoMantenimiento.EN_PROCESO)) {
                throw new IllegalArgumentException(
                        "El equipo " + equipo.getModelo() + " ya tiene un mantenimiento en proceso. Debe finalizarlo antes de crear uno nuevo.");
            }
            equiposValidados.add(equipo);
        }

        Integer idCliente = null;
        if (request.custodioId() != null) {
            CustodiosJpa custodio = custodiosRepo.findById(request.custodioId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Custodio no encontrado"));
            if (!custodio.isEstado()) {
                throw new IllegalArgumentException("El custodio no esta activo");
            }
            idCliente = custodio.getIdCustodio();
        }

        UsuariosJpa tecnico = usuariosRepo.findByCorreo(correoAutenticado)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario autenticado no encontrado"));

        EquiposJpa primerEquipo = equiposValidados.get(0);

        MantenimientosJpa entity = new MantenimientosJpa();
        entity.setEquipoId(primerEquipo.getIdEquipo());
        entity.setIdCliente(idCliente);
        entity.setIdUsuario(tecnico.getIdUsuario());
        entity.setTipoMantenimiento(request.tipoMantenimiento());
        entity.setFechaProgramada(request.fechaMantenimiento().atStartOfDay());
        entity.setDescripcion(request.detalle());
        entity.setEstadoGeneral(request.estadoGeneral());
        entity.setProximaFecha(request.proximaFecha());
        entity.setActivo(Boolean.TRUE);
        entity.setCreadoEn(LocalDateTime.now());
        entity.setEstadoInterno(EstadoInternoMantenimiento.EN_PROCESO);
        entity.setEstado("MANUAL");
        entity.setTipoOrigen(TipoOrigenMantenimiento.MANUAL);
        entity.setFkProgramado(request.idProgramado());
        entity.setSerieSnapshot(primerEquipo.getSerial());
        entity = mantenimientosRepo.save(entity);

        for (EquiposJpa eq : equiposValidados) {
            MantenimientoEquipoJpa me = new MantenimientoEquipoJpa();
            me.setMantenimientoId(entity.getIdMantenimiento());
            me.setEquipoId(eq.getIdEquipo());
            mantenimientoEquipoRepo.save(me);
        }

        guardarFirmas(entity.getIdMantenimiento(), request);
        guardarActividades(entity.getIdMantenimiento(), request.actividades());
        guardarImagenes(entity.getIdMantenimiento(), request.imagenes());
        return obtenerDetalle(entity.getIdMantenimiento());
    }

    @Transactional
    public void guardarImagenes(Integer idMantenimiento, List<ImagenMantenimientoComando> imagenes) {
        if (imagenes == null || imagenes.isEmpty()) {
            return;
        }
        List<ImagenMantenimientoJpa> entities = imagenes.stream().map(img -> {
            ImagenMantenimientoJpa entity = new ImagenMantenimientoJpa();
            entity.setIdMantenimiento(idMantenimiento);
            entity.setNombreArchivo(img.nombreArchivo());
            entity.setRutaArchivo(img.rutaArchivo());
            entity.setTamanioBytes(img.tamanioBytes());
            return entity;
        }).toList();
        imagenRepo.saveAll(entities);
    }

    public List<MantenimientoManualResponseDTO> listarTodos() {
        return mantenimientosRepo.findAllByOrderByFechaProgramadaDescIdMantenimientoDesc().stream()
                .map(m -> toDto(m, false))
                .toList();
    }

    public Pagina<MantenimientoManualResponseDTO> listarTodosPaginado(int pagina, int tamanio) {
        Page<MantenimientosJpa> page = mantenimientosRepo
                .findAllByOrderByFechaProgramadaDescIdMantenimientoDesc(PageRequest.of(pagina, tamanio));
        List<MantenimientoManualResponseDTO> contenido = page.getContent().stream()
                .map(m -> toDto(m, false))
                .toList();
        return new Pagina<>(contenido, page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages());
    }

    public List<MantenimientoManualResponseDTO> obtenerHistorial(Integer equipoId) {
        return mantenimientosRepo.findByEquipoIdIncluyendoMultipleOrderByCreadoEnDesc(equipoId).stream()
                .map(m -> toDto(m, false))
                .toList();
    }

    public List<MantenimientoManualResponseDTO> listarPorTecnico(Integer tecnicoId) {
        return mantenimientosRepo.findByIdUsuarioOrderByCreadoEnDesc(tecnicoId).stream()
                .map(m -> toDto(m, false))
                .toList();
    }

    public Pagina<MantenimientoManualResponseDTO> listarPorTecnicoPaginado(Integer tecnicoId, int pagina, int tamanio) {
        Page<MantenimientosJpa> page = mantenimientosRepo
                .findByIdUsuarioOrderByFechaProgramadaDescIdMantenimientoDesc(tecnicoId, PageRequest.of(pagina, tamanio));
        List<MantenimientoManualResponseDTO> contenido = page.getContent().stream()
                .map(m -> toDto(m, false))
                .toList();
        return new Pagina<>(contenido, page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages());
    }

    public MantenimientoManualResponseDTO obtenerDetalle(Integer idMantenimiento) {
        MantenimientosJpa mantenimiento = mantenimientosRepo.findById(idMantenimiento)
                .orElseThrow(() -> new RecursoNoEncontradoException("Mantenimiento no encontrado"));
        return toDto(mantenimiento, true);
    }

    @Transactional
    public MantenimientoManualResponseDTO cerrar(Integer idMantenimiento, String descripcionTrabajoRealizado,
            ResultadoTecnico resultadoTecnico, String cerradoPor) {
        if (resultadoTecnico == null) {
            throw new IllegalStateException("El resultado tecnico es obligatorio para cerrar la OT");
        }
        MantenimientosJpa mantenimiento = mantenimientosRepo.findById(idMantenimiento)
                .orElseThrow(() -> new RecursoNoEncontradoException("Mantenimiento no encontrado"));
        mantenimiento.setDescripcion(actualizarDescripcionCierre(
                mantenimiento.getDescripcion(),
                descripcionTrabajoRealizado));
        mantenimiento.setEstadoInterno(EstadoInternoMantenimiento.CERRADO);
        mantenimiento.setFecCierre(LocalDateTime.now());
        mantenimiento.setResultadoTecnico(resultadoTecnico);
        if (cerradoPor != null && !cerradoPor.isBlank()) {
            mantenimiento.setCerradoPor(cerradoPor);
        }
        mantenimientosRepo.save(mantenimiento);

        // Se recalcula el plan preventivo si la OT esta vinculada a uno (fk_programado,
        // seteado al crearla desde "Generar OT") o si es una OT preventiva creada
        // manualmente sin pasar por ese flujo. Cerrar una OT correctiva no debe
        // reiniciar el contador de mantenimiento preventivo del equipo.
        if (mantenimiento.getFkProgramado() != null
                || "PREVENTIVO".equalsIgnoreCase(mantenimiento.getTipoMantenimiento())) {
            List<MantenimientoEquipoJpa> equiposOrden = mantenimientoEquipoRepo.findByMantenimientoId(idMantenimiento);
            if (!equiposOrden.isEmpty()) {
                for (MantenimientoEquipoJpa me : equiposOrden) {
                    programadoService.recalcularProximaFecha(me.getEquipoId());
                }
            } else if (mantenimiento.getEquipoId() != null) {
                programadoService.recalcularProximaFecha(mantenimiento.getEquipoId());
            }
        }

        if (resultadoTecnico == ResultadoTecnico.IRREPARABLE
                || resultadoTecnico == ResultadoTecnico.REQUIERE_BAJA) {
            marcarBajaRecomendada(mantenimiento);
        }

        notificacionService.marcarRelacionadasComoLeidas(idMantenimiento);
        return toDto(mantenimiento, true);
    }

    private void marcarBajaRecomendada(MantenimientosJpa mantenimiento) {
        List<Integer> equipoIds = new ArrayList<>();
        List<MantenimientoEquipoJpa> equiposOrden = mantenimientoEquipoRepo
                .findByMantenimientoId(mantenimiento.getIdMantenimiento());
        if (!equiposOrden.isEmpty()) {
            equiposOrden.forEach(me -> equipoIds.add(me.getEquipoId()));
        } else if (mantenimiento.getEquipoId() != null) {
            equipoIds.add(mantenimiento.getEquipoId());
        }
        for (Integer equipoId : equipoIds) {
            equiposRepo.findById(equipoId).ifPresent(equipo -> {
                equipo.setBajaRecomendada(true);
                equipo.setBajaRecomendadaOrigen(mantenimiento.getIdMantenimiento());
                equiposRepo.save(equipo);
            });
        }
    }

    private void guardarActividades(Integer idMantenimiento, List<ActividadManualComando> actividades) {
        if (actividades == null || actividades.isEmpty()) {
            return;
        }
        MantenimientosJpa mantenimiento = mantenimientosRepo.findById(idMantenimiento)
                .orElseThrow(() -> new RecursoNoEncontradoException("Mantenimiento no encontrado"));
        Integer idCategoria = mantenimiento.getFkEquipo() != null && mantenimiento.getFkEquipo().getFkCategoria() != null
                ? mantenimiento.getFkEquipo().getFkCategoria().getIdCategoria()
                : null;
        List<ActividadChecklistJpa> checklistActivo = actividadChecklistRepo.findAllByEstadoTrueOrderByOrdenAsc();
        if (idCategoria != null) {
            List<ActividadChecklistJpa> checklistFiltrado = actividadChecklistRepo.findActivasPorCategoria(idCategoria);
            if (!checklistFiltrado.isEmpty()) {
                checklistActivo = checklistFiltrado;
            }
        }
        Map<Integer, ActividadChecklistJpa> checklistPorId = checklistActivo.stream()
                .collect(Collectors.toMap(ActividadChecklistJpa::getIdActividad, act -> act, (a, b) -> a, LinkedHashMap::new));

        List<ActividadRealizadaJpa> entities = new ArrayList<>();
        for (ActividadManualComando act : actividades) {
            Integer idActividadReal = resolverActividadReal(act, checklistActivo, checklistPorId);
            if (idActividadReal == null) {
                continue;
            }
            ActividadRealizadaJpa entity = new ActividadRealizadaJpa();
            entity.setIdMantenimiento(idMantenimiento);
            entity.setIdActividad(idActividadReal);
            entity.setRealizada(Boolean.TRUE.equals(act.realizada()));
            entities.add(entity);
        }
        actividadRealizadaRepo.saveAll(entities);
    }

    private Integer resolverActividadReal(ActividadManualComando actividad,
            List<ActividadChecklistJpa> checklistActivo,
            Map<Integer, ActividadChecklistJpa> checklistPorId) {
        if (actividad == null || actividad.idActividad() == null) {
            return null;
        }

        if (checklistPorId.containsKey(actividad.idActividad())) {
            return actividad.idActividad();
        }

        throw new IllegalArgumentException(
                "La actividad " + actividad.idActividad() + " no pertenece al checklist activo del mantenimiento");
    }

    private void guardarFirmas(Integer idMantenimiento, MantenimientoManualComando request) {
        if (request.firmaTecnico() != null && !request.firmaTecnico().isBlank()) {
            firmaMantenimientoRepositorio.guardar(new FirmaMantenimiento(
                    null,
                    idMantenimiento,
                    TipoFirma.TECNICO,
                    request.firmaTecnico(),
                    LocalDateTime.now(),
                    request.ipOrigen()));
        }
        if (request.firmaCustodio() != null && !request.firmaCustodio().isBlank()) {
            firmaMantenimientoRepositorio.guardar(new FirmaMantenimiento(
                    null,
                    idMantenimiento,
                    TipoFirma.CUSTODIO,
                    request.firmaCustodio(),
                    LocalDateTime.now(),
                    request.ipOrigen()));
        }
    }

    private MantenimientoManualResponseDTO toDto(MantenimientosJpa mantenimiento, boolean incluirDetalle) {
        Integer idCategoria = mantenimiento.getFkEquipo() != null && mantenimiento.getFkEquipo().getFkCategoria() != null
                ? mantenimiento.getFkEquipo().getFkCategoria().getIdCategoria()
                : null;
        List<ActividadChecklistJpa> checklist = idCategoria == null
                ? actividadChecklistRepo.findAllByEstadoTrueOrderByOrdenAsc()
                : actividadChecklistRepo.findActivasPorCategoria(idCategoria);
        if (checklist.isEmpty()) {
            checklist = actividadChecklistRepo.findAllByEstadoTrueOrderByOrdenAsc();
        }
        Map<Integer, Boolean> realizadas = actividadRealizadaRepo.findAllByIdMantenimiento(mantenimiento.getIdMantenimiento())
                .stream()
                .collect(Collectors.toMap(ActividadRealizadaJpa::getIdActividad, ar -> Boolean.TRUE.equals(ar.getRealizada())));
        List<ActividadManualResponseDTO> actividades = incluirDetalle
                ? checklist.stream()
                        .sorted(Comparator.comparing(ActividadChecklistJpa::getOrden, Comparator.nullsLast(Integer::compareTo)))
                        .map(act -> ActividadManualResponseDTO.builder()
                                .idActividad(act.getIdActividad())
                                .nombreActividad(act.getNombre())
                                .categoriaActividad(null)
                                .realizada(Boolean.TRUE.equals(realizadas.get(act.getIdActividad())))
                                .build())
                        .toList()
                : List.of();
        List<ImagenMantenimientoResponseDTO> imagenes = incluirDetalle
                ? imagenRepo.findByIdMantenimiento(mantenimiento.getIdMantenimiento()).stream()
                        .map(img -> ImagenMantenimientoResponseDTO.builder()
                                .idImagen(img.getIdImagen())
                                .nombreArchivo(img.getNombreArchivo())
                                .rutaArchivo(img.getRutaArchivo())
                                .tamanioBytes(img.getTamanioBytes())
                                .build())
                        .toList()
                : List.of();
        String firmaTecnico = firmaMantenimientoRepositorio
                .buscarPorMantenimientoYTipo(mantenimiento.getIdMantenimiento(), TipoFirma.TECNICO)
                .map(FirmaMantenimiento::firmaBase64)
                .orElse(null);
        String firmaCustodio = firmaMantenimientoRepositorio
                .buscarPorMantenimientoYTipo(mantenimiento.getIdMantenimiento(), TipoFirma.CUSTODIO)
                .map(FirmaMantenimiento::firmaBase64)
                .orElse(null);
        String detalle = extraerDetalleTecnico(mantenimiento.getDescripcion());
        String trabajoRealizado = extraerTrabajoRealizado(mantenimiento.getDescripcion());

        List<MantenimientoEquipoJpa> equiposOrden = mantenimientoEquipoRepo
                .findByMantenimientoId(mantenimiento.getIdMantenimiento());
        List<EquipoEnMantenimientoDTO> equiposDtoList;
        int totalEquipos;
        if (!equiposOrden.isEmpty()) {
            equiposDtoList = equiposOrden.stream()
                    .map(me -> EquipoEnMantenimientoDTO.builder()
                            .equipoId(me.getEquipoId())
                            .codigoSap(me.getEquipo() != null ? me.getEquipo().getCodigoSap() : null)
                            .descripcion(me.getEquipo() != null ? me.getEquipo().getModelo() : null)
                            .serial(me.getEquipo() != null ? me.getEquipo().getSerial() : null)
                            .build())
                    .toList();
            totalEquipos = equiposDtoList.size();
        } else {
            equiposDtoList = mantenimiento.getFkEquipo() != null
                    ? List.of(EquipoEnMantenimientoDTO.builder()
                            .equipoId(mantenimiento.getEquipoId())
                            .codigoSap(mantenimiento.getFkEquipo().getCodigoSap())
                            .descripcion(mantenimiento.getFkEquipo().getModelo())
                            .serial(mantenimiento.getFkEquipo().getSerial())
                            .build())
                    : List.of();
            totalEquipos = mantenimiento.getEquipoId() != null ? 1 : 0;
        }

        return MantenimientoManualResponseDTO.builder()
                .idMantenimiento(mantenimiento.getIdMantenimiento())
                .equipoId(mantenimiento.getEquipoId())
                .equipoCodigoSap(mantenimiento.getFkEquipo() != null ? mantenimiento.getFkEquipo().getCodigoSap() : null)
                .equipoDescripcion(mantenimiento.getFkEquipo() != null
                        ? mantenimiento.getFkEquipo().getModelo()
                        : null)
                .custodioId(mantenimiento.getIdCliente())
                .custodioNombre(mantenimiento.getFkCliente() != null ? mantenimiento.getFkCliente().getNombre() : null)
                .custodioCorreo(mantenimiento.getFkCliente() != null ? mantenimiento.getFkCliente().getCorreo() : null)
                .tecnicoId(mantenimiento.getIdUsuario())
                .tecnicoNombre(mantenimiento.getFkUsuario() != null ? mantenimiento.getFkUsuario().getNombre() : null)
                .tipoMantenimiento(mantenimiento.getTipoMantenimiento())
                .fechaMantenimiento(mantenimiento.getFechaProgramada() != null ? mantenimiento.getFechaProgramada().toLocalDate() : null)
                .detalle(detalle)
                .descripcionTrabajoRealizado(trabajoRealizado)
                .estadoGeneral(mantenimiento.getEstadoGeneral())
                .proximaFecha(mantenimiento.getProximaFecha())
                .firmaTecnico(firmaTecnico)
                .firmaCustodio(firmaCustodio)
                .estado(Boolean.TRUE.equals(mantenimiento.getActivo()))
                .estadoInterno(mantenimiento.getEstadoInterno() != null ? mantenimiento.getEstadoInterno().name() : null)
                .creadoEn(mantenimiento.getCreadoEn())
                .resultadoTecnico(mantenimiento.getResultadoTecnico())
                .cerradoPor(mantenimiento.getCerradoPor())
                .cerradoEn(mantenimiento.getFecCierre())
                .actividades(actividades)
                .imagenes(imagenes)
                .equipos(equiposDtoList)
                .totalEquipos(totalEquipos)
                .idProgramado(mantenimiento.getFkProgramado())
                .build();
    }

    private String actualizarDescripcionCierre(String descripcionActual, String descripcionTrabajoRealizado) {
        String detalle = extraerDetalleTecnico(descripcionActual);
        String trabajo = limpiar(descripcionTrabajoRealizado);
        if (trabajo == null) {
            return detalle;
        }
        if (detalle == null) {
            return TRABAJO_REALIZADO_LABEL + "\n" + trabajo;
        }
        return detalle + "\n\n" + TRABAJO_REALIZADO_LABEL + "\n" + trabajo;
    }

    private String extraerDetalleTecnico(String descripcion) {
        String limpia = limpiar(descripcion);
        if (limpia == null) {
            return null;
        }
        int markerIndex = limpia.indexOf(TRABAJO_REALIZADO_LABEL);
        if (markerIndex < 0) {
            return limpia;
        }
        String detalle = limpia.substring(0, markerIndex).trim();
        return detalle.isEmpty() ? null : detalle;
    }

    private String extraerTrabajoRealizado(String descripcion) {
        String limpia = limpiar(descripcion);
        if (limpia == null) {
            return null;
        }
        int markerIndex = limpia.indexOf(TRABAJO_REALIZADO_LABEL);
        if (markerIndex < 0) {
            return null;
        }
        String trabajo = limpia.substring(markerIndex + TRABAJO_REALIZADO_LABEL.length()).trim();
        return trabajo.isEmpty() ? null : trabajo;
    }

    private String limpiar(String texto) {
        if (texto == null) {
            return null;
        }
        String limpio = texto.trim();
        return limpio.isEmpty() ? null : limpio;
    }
}
