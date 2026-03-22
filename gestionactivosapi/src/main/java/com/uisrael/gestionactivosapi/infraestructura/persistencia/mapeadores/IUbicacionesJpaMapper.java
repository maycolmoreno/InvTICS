package com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.gestionactivosapi.dominio.entidades.Ubicaciones;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.UbicacionesJpa;

@Mapper(componentModel = "spring")
public interface IUbicacionesJpaMapper {

	Ubicaciones toDomain(UbicacionesJpa entity);

	UbicacionesJpa toEntity(Ubicaciones ubicacion);

}
