package com.uisrael.gestionactivosapi.infraestructura.repositorios;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.UbicacionTecnicoJpa;

public interface IUbicacionTecnicoJpaRepositorio extends JpaRepository<UbicacionTecnicoJpa, Long> {

    Optional<UbicacionTecnicoJpa> findTopByUsuarioIdOrderByTimestampCapturaDesc(Integer usuarioId);

    @Query("""
        SELECT u FROM UbicacionTecnicoJpa u
        WHERE u.timestampCaptura = (
            SELECT MAX(u2.timestampCaptura) FROM UbicacionTecnicoJpa u2
            WHERE u2.usuarioId = u.usuarioId
        )
        AND u.timestampCaptura >= :desde
        AND u.usuario.fkRol.nombre = 'TECNICO'
        """)
    List<UbicacionTecnicoJpa> findUltimaUbicacionPorTecnicosActivos(@Param("desde") LocalDateTime desde);

    @Modifying
    @Query("DELETE FROM UbicacionTecnicoJpa u WHERE u.timestampCaptura < :fecha")
    int deleteByTimestampCapturaBefore(@Param("fecha") LocalDateTime fecha);

    @Query("""
        SELECT u FROM UbicacionTecnicoJpa u
        WHERE u.timestampCaptura >= :desde
        AND u.timestampCaptura < :hasta
        AND u.usuario.fkRol.nombre = 'TECNICO'
        ORDER BY u.timestampCaptura DESC
        """)
    List<UbicacionTecnicoJpa> findHistorialPorFecha(
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta);
}
