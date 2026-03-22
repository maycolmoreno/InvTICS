package com.uisrael.gestionactivosapi.infraestructura.persistencia.adaptadores;

import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;

import com.uisrael.gestionactivosapi.dominio.entidades.EquipoVisita;
import com.uisrael.gestionactivosapi.dominio.repositorios.IEquipoVisitaRepositorio;

@Repository
public class EquipoVisitaRepositorioImpl implements IEquipoVisitaRepositorio {

    private final EntityManager entityManager;

    public EquipoVisitaRepositorioImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<EquipoVisita> findEquiposByUbicacionAndCustodio(Long ubicacionId, Long custodioId) {
        List<Object[]> data = entityManager.createQuery("""
                select
                    e.idEquipo,
                    c.idCustodio,
                    e.serial,
                    m.nombre,
                    e.modelo,
                    e.tipoEquipo,
                    e.codigoSap,
                    c.nombre,
                    d.nombre,
                    u.nombre,
                    (
                        select max(ma.fecCierre)
                        from MantenimientosJpa ma
                        where ma.equipoId = e.idEquipo
                          and ma.fecCierre is not null
                    )
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
                """, Object[].class)
                .setParameter("ubicacionId", ubicacionId.intValue())
                .setParameter("custodioId", custodioId != null ? custodioId.intValue() : null)
                .getResultList();

        return data.stream()
                .map(row -> new EquipoVisita(
                        (Integer) row[0],
                        (Integer) row[1],
                        (String) row[2],
                        (String) row[3],
                        (String) row[4],
                        (String) row[5],
                        (String) row[6],
                        (String) row[7],
                        (String) row[8],
                        (String) row[9],
                        (java.time.LocalDateTime) row[10]))
                .toList();
    }
}
