package com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores;

import com.uisrael.gestionactivosapi.dominio.entidades.Activo;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.ActivoJpa;

public interface IActivoJpaMapper {
	ActivoJpa toPersistence(Activo dominio);
	Activo toDomain(ActivoJpa persistencia);
}
