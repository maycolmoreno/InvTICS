package com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios;

import java.util.List;

import jakarta.persistence.EntityManager;
import com.uisrael.gestionactivosapi.dominio.entidades.EquipoVisita;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.EquipoVisitaRepositorioPuerto;

public class EquipoVisitaRepositorioImpl implements EquipoVisitaRepositorioPuerto {

	private final EntityManager entityManager;

	public EquipoVisitaRepositorioImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public List<EquipoVisita> findEquiposByUbicacionAndCustodio(Long ubicacionId, Long custodioId) {
		// Custom query using EntityManager - implement based on actual DB structure
		String jpql = "SELECT e FROM EquipoVisita e WHERE e.ubicacion.id = :ubicacionId AND e.custodio.id = :custodioId";
		return entityManager.createQuery(jpql, EquipoVisita.class)
				.setParameter("ubicacionId", ubicacionId)
				.setParameter("custodioId", custodioId)
				.getResultList();
	}
}
