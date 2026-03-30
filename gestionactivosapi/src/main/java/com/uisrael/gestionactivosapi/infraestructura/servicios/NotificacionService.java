package com.uisrael.gestionactivosapi.infraestructura.servicios;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.uisrael.gestionactivosapi.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.NotificacionJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.UsuariosJpa;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IMantenimientosJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.INotificacionJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IUsuariosJpaRepositorio;
import com.uisrael.gestionactivosapi.presentacion.dto.response.NotificacionResponseDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NotificacionService {

    private final INotificacionJpaRepositorio notificacionRepo;
    private final IUsuariosJpaRepositorio usuariosRepo;
    private final IMantenimientosJpaRepositorio mantenimientosRepo;

    public NotificacionResponseDTO crear(Integer usuarioId, String mensaje, String url, Integer mantenimientoId) {
        UsuariosJpa usuario = usuariosRepo.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        if (mantenimientoId != null && !notificacionRepo.existsMantenimientoById(mantenimientoId)) {
            throw new RecursoNoEncontradoException("Mantenimiento no encontrado para notificacion");
        }
        NotificacionJpa entity = new NotificacionJpa();
        entity.setUsuarioId(usuario.getIdUsuario());
        entity.setMensaje(mensaje);
        entity.setUrl(url);
        entity.setLeida(Boolean.FALSE);
        entity.setCreadoEn(LocalDateTime.now());
        entity.setReferenciaMantenimientoId(mantenimientoId);
        if (mantenimientoId != null) {
            entity.setFkMantenimiento(mantenimientosRepo.findById(mantenimientoId).orElse(null));
        }
        return toDto(notificacionRepo.save(entity));
    }

    public Integer obtenerUsuarioIdPorCorreo(String correo) {
        return usuariosRepo.findByCorreo(correo)
                .map(UsuariosJpa::getIdUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario autenticado no encontrado"));
    }

    public long contarNoLeidas(Integer usuarioId) {
        return notificacionRepo.countByUsuarioIdAndLeidaFalse(usuarioId);
    }

    public List<NotificacionResponseDTO> listarPorUsuario(Integer usuarioId) {
        return notificacionRepo.findByUsuarioIdOrderByCreadoEnDesc(usuarioId).stream().map(this::toDto).toList();
    }

    public void marcarLeida(Long id) {
        NotificacionJpa entity = notificacionRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Notificacion no encontrada"));
        entity.setLeida(Boolean.TRUE);
        notificacionRepo.save(entity);
    }

    public void marcarRelacionadasComoLeidas(Integer mantenimientoId) {
        List<NotificacionJpa> entities = notificacionRepo.findByReferenciaMantenimientoId(mantenimientoId);
        entities.forEach(n -> n.setLeida(Boolean.TRUE));
        notificacionRepo.saveAll(entities);
    }

    private NotificacionResponseDTO toDto(NotificacionJpa entity) {
        return NotificacionResponseDTO.builder()
                .idNotificacion(entity.getIdNotificacion())
                .usuarioId(entity.getUsuarioId())
                .mensaje(entity.getMensaje())
                .url(entity.getUrl())
                .leida(Boolean.TRUE.equals(entity.getLeida()))
                .creadoEn(entity.getCreadoEn())
                .referenciaMantenimientoId(entity.getReferenciaMantenimientoId())
                .build();
    }
}
