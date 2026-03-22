package com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.gestionactivosapi.dominio.entidades.CategoriaEquipos;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CategoriaEquiposJpa;

@Mapper(componentModel = "spring")
public interface ICategoriaEquiposJpaMapper {

	@Mapping(source = "idCategoria", target = "idCategoria")
	CategoriaEquipos toDomain(CategoriaEquiposJpa entity);

	@Mapping(source = "idCategoria", target = "idCategoria")
	CategoriaEquiposJpa toEntity(CategoriaEquipos categoriaEquipo);

}
