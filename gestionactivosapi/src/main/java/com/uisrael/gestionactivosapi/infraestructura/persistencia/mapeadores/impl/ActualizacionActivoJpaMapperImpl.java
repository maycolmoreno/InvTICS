package com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.impl;

import org.springframework.stereotype.Component;

import com.uisrael.gestionactivosapi.dominio.entidades.ActualizacionActivo;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.ActualizacionActivoJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.IActualizacionActivoJpaMapper;

@Component
public class ActualizacionActivoJpaMapperImpl implements IActualizacionActivoJpaMapper {

	@Override
	public ActualizacionActivoJpa toPersistence(ActualizacionActivo dominio) {
		if (dominio == null) {
			return null;
		}

		ActualizacionActivoJpa jpa = new ActualizacionActivoJpa();
		jpa.setId(dominio.getId());
		jpa.setActivoId(dominio.getActivoId());
		jpa.setFechaActualizacion(dominio.getFechaActualizacion());
		jpa.setDescripcion(dominio.getDescripcion());
		// usuarioActualizacion (String en dominio) ya no existe en JPA; ahora es fkUsuarioActualizacion (Integer)
		return jpa;
	}

	@Override
	public ActualizacionActivo toDomain(ActualizacionActivoJpa persistencia) {
		if (persistencia == null) {
			return null;
		}

		ActualizacionActivo dominio = new ActualizacionActivo();
		dominio.setId(persistencia.getId() != null ? persistencia.getId() : 0);
		dominio.setActivoId(persistencia.getActivoId() != null ? persistencia.getActivoId() : 0);
		dominio.setFechaActualizacion(persistencia.getFechaActualizacion());
		dominio.setDescripcion(persistencia.getDescripcion());
		// Resolver nombre del usuario vía relación JPA si está disponible
		dominio.setUsuarioActualizacion(persistencia.getUsuarioRel() != null ? persistencia.getUsuarioRel().getNombre() : null);
		return dominio;
	}
}
