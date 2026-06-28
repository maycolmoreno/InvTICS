package com.uisrael.gestionactivosapi.infraestructura.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.EquiposJpa;

public interface IEquiposJpaRepositorio extends JpaRepository<EquiposJpa, Integer> {

	@Override
	@EntityGraph(attributePaths = {"fkMarcas", "fkCategoria"})
	List<EquiposJpa> findAll();

	@EntityGraph(attributePaths = {"fkMarcas", "fkCategoria"})
	Page<EquiposJpa> findAll(Pageable pageable);

	@Override
	@EntityGraph(attributePaths = {"fkMarcas", "fkCategoria"})
	Optional<EquiposJpa> findById(Integer id);

	boolean existsByCodigoSapIgnoreCase(String codigo);

	boolean existsByCodigoCresioIgnoreCase(String codigo);

	boolean existsByCodigoCresioIgnoreCaseAndIdEquipoNot(String codigo, Integer idEquipo);

	Optional<EquiposJpa> findFirstByCodigoCresioStartingWithOrderByCodigoCresioDesc(String prefijo);

	@EntityGraph(attributePaths = {"fkMarcas", "fkCategoria", "bodegaActual", "ordenCompra"})
	List<EquiposJpa> findByEstadoInventarioAndEstadoTrue(String estadoInventario);

	@EntityGraph(attributePaths = {"fkMarcas", "fkCategoria"})
	List<EquiposJpa> findByEstadoInventarioIsNullAndEstadoTrue();

	boolean existsByCodigoSapIgnoreCaseAndIdEquipoNot(String codigo, Integer idEquipo);

	boolean existsBySerialIgnoreCase(String serial);

	boolean existsBySerialIgnoreCaseAndIdEquipoNot(String serial, Integer idEquipo);

	boolean existsByMacIgnoreCase(String mac);

	boolean existsByMacIgnoreCaseAndIdEquipoNot(String mac, Integer idEquipo);

}
