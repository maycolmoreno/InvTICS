package com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores;

import com.uisrael.gestionactivosapi.dominio.entidades.ActualizacionActivo;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.ActualizacionActivoJpa;

public interface IActualizacionActivoJpaMapper {
	ActualizacionActivoJpa toPersistence(ActualizacionActivo dominio);
	ActualizacionActivo toDomain(ActualizacionActivoJpa persistencia);
}
