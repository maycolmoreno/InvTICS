package com.uisrael.gestionactivosapi.infraestructura.repositorios;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CustodiasJpa;

public interface ICustodiasJpaRepositorio extends JpaRepository<CustodiasJpa, Integer> {

    @Override
    @EntityGraph(attributePaths = {"fkEquipo", "fkEquipo.fkMarcas", "fkEquipo.fkCategoria", "fkEquipo.fkUbicacion",
            "fkCustodio", "fkCustodio.fkCargo", "fkCustodio.fkCargo.fkDepartamento", "fkCustodio.fkUbicacion",
            "fkUbicacion"})
    java.util.List<CustodiasJpa> findAll();

    @EntityGraph(attributePaths = {"fkEquipo", "fkEquipo.fkMarcas", "fkEquipo.fkCategoria", "fkEquipo.fkUbicacion",
            "fkCustodio", "fkCustodio.fkCargo", "fkCustodio.fkCargo.fkDepartamento", "fkCustodio.fkUbicacion",
            "fkUbicacion"})
    Page<CustodiasJpa> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"fkEquipo", "fkEquipo.fkMarcas", "fkEquipo.fkCategoria", "fkEquipo.fkUbicacion",
            "fkCustodio", "fkCustodio.fkCargo", "fkCustodio.fkCargo.fkDepartamento", "fkCustodio.fkUbicacion",
            "fkUbicacion"})
    java.util.Optional<CustodiasJpa> findById(Integer id);

    boolean existsByFkEquipo_IdEquipoAndEstadoTrue(Integer idEquipo);

    boolean existsByFkEquipo_IdEquipoAndEstadoTrueAndIdCustodiaEquipoNot(Integer idEquipo, Integer idCustodiaEquipo);

    long countByTipoMovimiento(String tipoMovimiento);

    java.util.Optional<CustodiasJpa> findFirstByFkEquipo_IdEquipoAndEstadoTrueAndFechaFinIsNullOrderByIdCustodiaEquipoDesc(
            Integer idEquipo);
}
