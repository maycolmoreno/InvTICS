package com.uisrael.gestionactivosapi.infraestructura.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CustodiasJpa;

public interface ICustodiasJpaRepositorio extends JpaRepository<CustodiasJpa, Integer> {

    boolean existsByFkEquipo_IdEquipoAndEstadoTrue(Integer idEquipo);

    boolean existsByFkEquipo_IdEquipoAndEstadoTrueAndIdCustodiaEquipoNot(Integer idEquipo, Integer idCustodiaEquipo);

    long countByTipoMovimiento(String tipoMovimiento);

    java.util.Optional<CustodiasJpa> findFirstByFkEquipo_IdEquipoAndEstadoTrueAndFechaFinIsNullOrderByIdCustodiaEquipoDesc(
            Integer idEquipo);
}
