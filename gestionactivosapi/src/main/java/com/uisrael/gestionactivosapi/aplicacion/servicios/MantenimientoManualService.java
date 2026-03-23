package com.uisrael.gestionactivosapi.aplicacion.servicios;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uisrael.gestionactivosapi.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.entidades.EstadoInternoMantenimiento;
import com.uisrael.gestionactivosapi.dominio.entidades.FirmaMantenimiento;
import com.uisrael.gestionactivosapi.dominio.entidades.TipoFirma;
import com.uisrael.gestionactivosapi.dominio.entidades.TipoOrigenMantenimiento;
import com.uisrael.gestionactivosapi.dominio.repositorios.IFirmaMantenimientoRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.ActividadChecklistJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.ActividadRealizadaJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CustodiosJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.EquiposJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.ImagenMantenimientoJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.MantenimientosJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.UsuariosJpa;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IActividadChecklistJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IActividadRealizadaJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.ICustodiosJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IEquiposJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IImagenMantenimientoJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IMantenimientosJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IUsuariosJpaRepositorio;
import com.uisrael.gestionactivosapi.presentacion.dto.request.ActividadManualRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.ImagenMantenimientoRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.MantenimientoManualRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.ActividadManualResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.ImagenMantenimientoResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.MantenimientoManualResponseDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MantenimientoManualService {

    private static final String TRABAJO_REALIZADO_LABEL = "Trabajo realizado:";

    private final IMantenimientosJpaRepositorio mantenimientosRepo;
    private final IActividadRealizadaJpaRepositorio actividadRealizadaRepo;
    private final IActividadChecklistJpaRepositorio actividadChecklistRepo;
    private final IImagenMantenimientoJpaRepositorio imagenRepo;
    private final IEquiposJpaRepositorio equiposRepo;
    private final ICustodiosJpaRepositorio custodiosRepo;
    private final IUsuariosJpaRepositorio usuariosRepo;
    private final MantenimientoProgramadoService programadoService;
    private final NotificacionService notificacionService;
    private final IFirmaMantenimientoRepositorio firmaMantenimientoRepositorio;

    @Transactional
    public MantenimientoManualResponseDTO crear(MantenimientoManualRequestDTO request, String correoAutenticado) {
        EquiposJpa equipo = equiposRepo.findById(request.getEquipoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Equipo no encontrado"));
        if (!equipo.isEstado()) {
            throw new IllegalArgumentException("El equipo no esta activo");
        }
        CustodiosJpa custodio = custodiosRepo.findById(request.getCustodioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Custodio no encontrado"));
        if (!custodio.isEstado()) {
            throw new IllegalArgumentException("El custodio no esta activo");
        }
        UsuariosJpa tecnico = usuariosRepo.findByCorreo(correoAutenticado)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario autenticado no encontrado"));

        MantenimientosJpa entity = new MantenimientosJpa();
        entity.setEquipoId(equipo.getIdEquipo());
        entity.setIdCliente(custodio.getIdCustodio());
        entity.setIdUsuario(tecnico.getIdUsuario());
        entity.setTipoMantenimiento(request.getTipoMantenimiento());
        entity.setFechaProgramada(request.getFechaMantenimiento().atStartOfDay());
        entity.setDescripcion(request.getDetalle());
        entity.setEstadoGeneral(request.getEstadoGeneral());
        entity.setProximaFecha(request.getProximaFecha());
        entity.setActivo(Boolean.TRUE);
        entity.setCreadoEn(LocalDateTime.now());
        entity.setEstadoInterno(EstadoInternoMantenimiento.EN_PROCESO);
        entity.setEstado("MANUAL");
        entity.setTipoOrigen(TipoOrigenMantenimiento.MANUAL);
        entity.setSerieSnapshot(equipo.getSerial());
        entity = mantenimientosRepo.save(entity);
        guardarFirmas(entity.getIdMantenimiento(), request);

        guardarActividades(entity.getIdMantenimiento(), request.getActividades());
        guardarImagenes(entity.getIdMantenimiento(), request.getImagenes());
        return obtenerDetalle(entity.getIdMantenimiento());
    }

    @Transactional
    public void guardarImagenes(Integer idMantenimiento, List<ImagenMantenimientoRequestDTO> imagenes) {
        if (imagenes == null || imagenes.isEmpty()) {
            return;
        }
        List<ImagenMantenimientoJpa> entities = imagenes.stream().map(img -> {
            ImagenMantenimientoJpa entity = new ImagenMantenimientoJpa();
            entity.setIdMantenimiento(idMantenimiento);
            entity.setNombreArchivo(img.getNombreArchivo());
            entity.setRutaArchivo(img.getRutaArchivo());
            entity.setTamanioBytes(img.getTamanioBytes());
            return entity;
        }).toList();
        imagenRepo.saveAll(entities);
    }

    public List<MantenimientoManualResponseDTO> listarTodos() {
        return mantenimientosRepo.findAllByOrderByFechaProgramadaDescIdMantenimientoDesc().stream()
                .map(m -> toDto(m, false))
                .toList();
    }

    public List<MantenimientoManualResponseDTO> obtenerHistorial(Integer equipoId) {
        return mantenimientosRepo.findByEquipoIdOrderByCreadoEnDesc(equipoId).stream()
                .map(m -> toDto(m, false))
                .toList();
    }

    public MantenimientoManualResponseDTO obtenerDetalle(Integer idMantenimiento) {
        MantenimientosJpa mantenimiento = mantenimientosRepo.findById(idMantenimiento)
                .orElseThrow(() -> new RecursoNoEncontradoException("Mantenimiento no encontrado"));
        return toDto(mantenimiento, true);
    }

    @Transactional
    public MantenimientoManualResponseDTO cerrar(Integer idMantenimiento, String descripcionTrabajoRealizado) {
        MantenimientosJpa mantenimiento = mantenimientosRepo.findById(idMantenimiento)
                .orElseThrow(() -> new RecursoNoEncontradoException("Mantenimiento no encontrado"));
        mantenimiento.setDescripcion(actualizarDescripcionCierre(
                mantenimiento.getDescripcion(),
                descripcionTrabajoRealizado));
        mantenimiento.setEstadoInterno(EstadoInternoMantenimiento.CERRADO);
        mantenimiento.setFecCierre(LocalDateTime.now());
        mantenimientosRepo.save(mantenimiento);
        if (mantenimiento.getEquipoId() != null) {
            programadoService.recalcularProximaFecha(mantenimiento.getEquipoId());
        }
        notificacionService.marcarRelacionadasComoLeidas(idMantenimiento);
        return toDto(mantenimiento, true);
    }

    private void guardarActividades(Integer idMantenimiento, List<ActividadManualRequestDTO> actividades) {
        if (actividades == null || actividades.isEmpty()) {
            return;
        }
        MantenimientosJpa mantenimiento = mantenimientosRepo.findById(idMantenimiento)
                .orElseThrow(() -> new RecursoNoEncontradoException("Mantenimiento no encontrado"));
        Integer idCategoria = mantenimiento.getFkEquipo() != null && mantenimiento.getFkEquipo().getFkCategoria() != null
                ? mantenimiento.getFkEquipo().getFkCategoria().getIdCategoria()
                : null;
        List<ActividadChecklistJpa> checklistActivo = actividadChecklistRepo.findAllByEstadoTrueOrderByCategoriaAscOrdenAsc();
        if (idCategoria != null) {
            List<ActividadChecklistJpa> checklistFiltrado = actividadChecklistRepo.findActivasPorCategoria(idCategoria);
            if (!checklistFiltrado.isEmpty()) {
                checklistActivo = checklistFiltrado;
            }
        }
        Map<Integer, ActividadChecklistJpa> checklistPorId = checklistActivo.stream()
                .collect(Collectors.toMap(ActividadChecklistJpa::getIdActividad, act -> act, (a, b) -> a, LinkedHashMap::new));

        List<ActividadRealizadaJpa> entities = new ArrayList<>();
        for (ActividadManualRequestDTO act : actividades) {
            Integer idActividadReal = resolverActividadReal(act, checklistActivo, checklistPorId);
            if (idActividadReal == null) {
                continue;
            }
            ActividadRealizadaJpa entity = new ActividadRealizadaJpa();
            entity.setIdMantenimiento(idMantenimiento);
            entity.setIdActividad(idActividadReal);
            entity.setRealizada(Boolean.TRUE.equals(act.getRealizada()));
            entities.add(entity);
        }
        actividadRealizadaRepo.saveAll(entities);
    }

    private Integer resolverActividadReal(ActividadManualRequestDTO actividad,
            List<ActividadChecklistJpa> checklistActivo,
            Map<Integer, ActividadChecklistJpa> checklistPorId) {
        if (actividad == null || actividad.getIdActividad() == null) {
            return null;
        }

        if (checklistPorId.containsKey(actividad.getIdActividad())) {
            return actividad.getIdActividad();
        }

        throw new IllegalArgumentException(
                "La actividad " + actividad.getIdActividad() + " no pertenece al checklist activo del mantenimiento");
    }

    private void guardarFirmas(Integer idMantenimiento, MantenimientoManualRequestDTO request) {
        if (request.getFirmaTecnico() != null && !request.getFirmaTecnico().isBlank()) {
            firmaMantenimientoRepositorio.guardar(new FirmaMantenimiento(
                    null,
                    idMantenimiento,
                    TipoFirma.TECNICO,
                    request.getFirmaTecnico(),
                    LocalDateTime.now(),
                    request.getIpOrigen()));
        }
        if (request.getFirmaCustodio() != null && !request.getFirmaCustodio().isBlank()) {
            firmaMantenimientoRepositorio.guardar(new FirmaMantenimiento(
                    null,
                    idMantenimiento,
                    TipoFirma.CUSTODIO,
                    request.getFirmaCustodio(),
                    LocalDateTime.now(),
                    request.getIpOrigen()));
        }
    }

    private MantenimientoManualResponseDTO toDto(MantenimientosJpa mantenimiento, boolean incluirDetalle) {
        Integer idCategoria = mantenimiento.getFkEquipo() != null && mantenimiento.getFkEquipo().getFkCategoria() != null
                ? mantenimiento.getFkEquipo().getFkCategoria().getIdCategoria()
                : null;
        List<ActividadChecklistJpa> checklist = idCategoria == null
                ? actividadChecklistRepo.findAllByEstadoTrueOrderByCategoriaAscOrdenAsc()
                : actividadChecklistRepo.findActivasPorCategoria(idCategoria);
        if (checklist.isEmpty()) {
            checklist = actividadChecklistRepo.findAllByEstadoTrueOrderByCategoriaAscOrdenAsc();
        }
        Map<Integer, Boolean> realizadas = actividadRealizadaRepo.findAllByIdMantenimiento(mantenimiento.getIdMantenimiento())
                .stream()
                .collect(Collectors.toMap(ActividadRealizadaJpa::getIdActividad, ar -> Boolean.TRUE.equals(ar.getRealizada())));
        List<ActividadManualResponseDTO> actividades = incluirDetalle
                ? checklist.stream()
                        .sorted(Comparator.comparing(ActividadChecklistJpa::getCategoria)
                                .thenComparing(ActividadChecklistJpa::getOrden, Comparator.nullsLast(Integer::compareTo)))
                        .map(act -> ActividadManualResponseDTO.builder()
                                .idActividad(act.getIdActividad())
                                .nombreActividad(act.getNombre())
                                .categoriaActividad(act.getCategoria())
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

        return MantenimientoManualResponseDTO.builder()
                .idMantenimiento(mantenimiento.getIdMantenimiento())
                .equipoId(mantenimiento.getEquipoId())
                .equipoCodigoSap(mantenimiento.getFkEquipo() != null ? mantenimiento.getFkEquipo().getCodigoSap() : null)
                .equipoDescripcion(mantenimiento.getFkEquipo() != null
                        ? (mantenimiento.getFkEquipo().getTipoEquipo() + " " + mantenimiento.getFkEquipo().getModelo())
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
                .actividades(actividades)
                .imagenes(imagenes)
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
