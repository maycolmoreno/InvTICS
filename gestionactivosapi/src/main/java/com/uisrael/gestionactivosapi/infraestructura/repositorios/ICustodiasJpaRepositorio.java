package com.uisrael.gestionactivosapi.infraestructura.repositorios;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CustodiasJpa;

public interface ICustodiasJpaRepositorio extends JpaRepository<CustodiasJpa, Integer> {

    @Override
    @EntityGraph(attributePaths = {"fkEquipo", "fkCustodio", "fkUbicacion"})
    java.util.List<CustodiasJpa> findAll();

    @EntityGraph(attributePaths = {"fkEquipo", "fkCustodio", "fkUbicacion"})
    Page<CustodiasJpa> findAll(Pageable pageable);

    boolean existsByFkEquipo_IdEquipoAndEstadoTrue(Integer idEquipo);

    boolean existsByFkEquipo_IdEquipoAndEstadoTrueAndIdCustodiaEquipoNot(Integer idEquipo, Integer idCustodiaEquipo);

    long countByTipoMovimiento(String tipoMovimiento);

    java.util.Optional<CustodiasJpa> findFirstByFkEquipo_IdEquipoAndEstadoTrueAndFechaFinIsNullOrderByIdCustodiaEquipoDesc(
            Integer idEquipo);
}
