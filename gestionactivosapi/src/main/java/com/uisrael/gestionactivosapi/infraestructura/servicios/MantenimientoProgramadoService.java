package com.uisrael.gestionactivosapi.infraestructura.servicios;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.uisrael.gestionactivosapi.dominio.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.EquiposJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.MantenimientoProgramadoJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.UsuariosJpa;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IEquiposJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IMantenimientoProgramadoJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IUsuariosJpaRepositorio;
import com.uisrael.gestionactivosapi.presentacion.dto.request.MantenimientoProgramadoRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.MantenimientoProgramadoResponseDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MantenimientoProgramadoService {

    private final IMantenimientoProgramadoJpaRepositorio programadoRepo;
    private final IEquiposJpaRepositorio equiposRepo;
    private final IUsuariosJpaRepositorio usuariosRepo;

    public MantenimientoProgramadoResponseDTO programar(MantenimientoProgramadoRequestDTO request) {
        EquiposJpa equipo = equiposRepo.findById(request.getEquipoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Equipo no encontrado"));
        UsuariosJpa tecnico = usuariosRepo.findById(request.getTecnicoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Tecnico no encontrado"));

        MantenimientoProgramadoJpa entity = programadoRepo.findByEquipoId(request.getEquipoId())
                .orElseGet(MantenimientoProgramadoJpa::new);
        entity.setEquipoId(equipo.getIdEquipo());
        entity.setTecnicoId(tecnico.getIdUsuario());
        entity.setFrecuenciaDias(request.getFrecuenciaDias());
        entity.setFechaProximoMantenimiento(LocalDate.now().plusDays(request.getFrecuenciaDias()));
        entity.setEstado(Boolean.TRUE);
        entity.setObservaciones(request.getObservaciones());
        return toDto(programadoRepo.save(entity));
    }

    public void recalcularProximaFecha(Integer equipoId) {
        programadoRepo.findByEquipoId(equipoId).ifPresent(programado -> {
            LocalDate hoy = LocalDate.now();
            programado.setFechaUltimoMantenimiento(hoy);
            programado.setFechaProximoMantenimiento(hoy.plusDays(programado.getFrecuenciaDias()));
            programadoRepo.save(programado);
        });
    }

    public List<MantenimientoProgramadoResponseDTO> listarTodos() {
        return programadoRepo.findAll().stream().map(this::toDto).toList();
    }

    public List<MantenimientoProgramadoResponseDTO> obtenerVencidosYProximos() {
        LocalDate hoy = LocalDate.now();
        return programadoRepo.findByFechaProximoMantenimientoBetweenAndEstadoTrue(hoy.minusYears(1), hoy.plusDays(7))
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<MantenimientoProgramadoJpa> obtenerPendientesParaNotificar() {
        return programadoRepo.findByFechaProximoMantenimientoLessThanEqualAndEstadoTrue(LocalDate.now().plusDays(1));
    }

    public void desactivar(Long idProgramado) {
        MantenimientoProgramadoJpa entity = programadoRepo.findById(idProgramado)
                .orElseThrow(() -> new RecursoNoEncontradoException("Programacion no encontrada"));
        entity.setEstado(Boolean.FALSE);
        programadoRepo.save(entity);
    }

    public MantenimientoProgramadoResponseDTO obtenerPorEquipo(Integer equipoId) {
        return programadoRepo.findByEquipoId(equipoId)
                .map(this::toDto)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No hay programacion de mantenimiento para el equipo " + equipoId));
    }

    private MantenimientoProgramadoResponseDTO toDto(MantenimientoProgramadoJpa entity) {
        return MantenimientoProgramadoResponseDTO.builder()
                .idProgramado(entity.getIdProgramado())
                .equipoId(entity.getEquipoId())
                .equipoCodigoSap(entity.getFkEquipo() != null ? entity.getFkEquipo().getCodigoSap() : null)
                .equipoDescripcion(entity.getFkEquipo() != null
                        ? entity.getFkEquipo().getModelo()
                        : null)
                .tecnicoId(entity.getTecnicoId())
                .tecnicoNombre(entity.getFkTecnicoAsignado() != null ? entity.getFkTecnicoAsignado().getNombre() : null)
                .frecuenciaDias(entity.getFrecuenciaDias())
                .fechaUltimoMantenimiento(entity.getFechaUltimoMantenimiento())
                .fechaProximoMantenimiento(entity.getFechaProximoMantenimiento())
                .estado(Boolean.TRUE.equals(entity.getEstado()))
                .observaciones(entity.getObservaciones())
                .build();
    }
}
