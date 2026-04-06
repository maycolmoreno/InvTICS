package com.uisrael.gestionactivosapi.infraestructura.repositorios;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.NotificacionJpa;

public interface INotificacionJpaRepositorio extends JpaRepository<NotificacionJpa, Long> {

    long countByUsuarioIdAndLeidaFalse(Integer usuarioId);

    List<NotificacionJpa> findByUsuarioIdOrderByCreadoEnDesc(Integer usuarioId);

    Page<NotificacionJpa> findByUsuarioIdOrderByCreadoEnDesc(Integer usuarioId, Pageable pageable);

    List<NotificacionJpa> findByReferenciaMantenimientoId(Integer referenciaMantenimientoId);

    @Query("select count(m) > 0 from MantenimientosJpa m where m.idMantenimiento = :id")
    boolean existsMantenimientoById(@Param("id") Integer id);
}
