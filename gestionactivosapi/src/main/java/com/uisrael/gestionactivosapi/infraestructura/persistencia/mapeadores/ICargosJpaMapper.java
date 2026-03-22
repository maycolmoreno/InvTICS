package com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.gestionactivosapi.dominio.entidades.Cargos;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CargosJpa;

@Mapper(componentModel = "spring", uses = {IDepartamentosJpaMapper.class})
public interface ICargosJpaMapper {

	Cargos toDomain(CargosJpa entity);

	CargosJpa toEntity(Cargos cargos);
}
