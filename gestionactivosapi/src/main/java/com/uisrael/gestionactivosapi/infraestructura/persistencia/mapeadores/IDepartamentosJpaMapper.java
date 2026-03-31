package com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.gestionactivosapi.dominio.entidades.Departamentos;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.DepartamentosJpa;

@Mapper(componentModel = "spring")
public interface IDepartamentosJpaMapper {

	Departamentos toDomain(DepartamentosJpa entity);

	DepartamentosJpa toEntity(Departamentos departamento);
}
