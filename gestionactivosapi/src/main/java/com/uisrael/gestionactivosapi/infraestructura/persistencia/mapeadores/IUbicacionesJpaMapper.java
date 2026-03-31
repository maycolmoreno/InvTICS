package com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.gestionactivosapi.dominio.entidades.Ubicaciones;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.UbicacionesJpa;

@Mapper(componentModel = "spring", uses = {IDepartamentosJpaMapper.class})
public interface IUbicacionesJpaMapper {

	Ubicaciones toDomain(UbicacionesJpa entity);

	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	@Mapping(target = "deletedAt", ignore = true)
	UbicacionesJpa toEntity(Ubicaciones ubicacion);

}
