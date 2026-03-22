package com.uisrael.gestionactivosapi.infraestructura.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CustodiasJpa;

public interface IEquipoVisitaJpaRepositorio extends JpaRepository<CustodiasJpa, Integer> {

    @Query("""
            select
                e.idEquipo as idEquipo,
                c.idCustodio as idCustodio,
                e.serial as serial,
                m.nombre as marca,
                e.modelo as modelo,
                e.tipoEquipo as tipoEquipo,
                e.codigoSap as codigoSap,
                c.nombre as custodioNombre,
                d.nombre as custodioArea,
                u.nombre as ubicacionNombre,
                (
                    select max(ma.fecCierre)
                    from MantenimientosJpa ma
                    where ma.equipoId = e.idEquipo
                      and ma.fecCierre is not null
                ) as fechaUltimoMantenimiento
            from CustodiasJpa ce
                join ce.fkEquipo e
                join e.fkMarcas m
                join ce.fkCustodio c
                left join c.fkCargo cargo
                left join cargo.fkDepartamento d
                join ce.fkUbicacion u
            where ce.estado = true
              and ce.fechaFin is null
              and u.idUbicacion = :ubicacionId
              and (:custodioId is null or c.idCustodio = :custodioId)
            """)
    List<EquipoVisitaProjection> findEquiposVisita(@Param("ubicacionId") Long ubicacionId,
            @Param("custodioId") Long custodioId);
}
