package com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.gestionactivosapi.dominio.entidades.Roles;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.RolesJpa;

@Mapper(componentModel = "spring")
public interface IRolesJpaMapper {

	@Mapping(source = "idRol", target = "idRol")
	Roles toDomain(RolesJpa entity);

	@Mapping(source = "idRol", target = "idRol")
	RolesJpa toEntity(Roles rol);

}
