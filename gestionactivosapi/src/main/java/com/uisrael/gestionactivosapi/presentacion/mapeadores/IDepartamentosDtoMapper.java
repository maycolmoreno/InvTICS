package com.uisrael.gestionactivosapi.presentacion.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.gestionactivosapi.dominio.entidades.Departamentos;
import com.uisrael.gestionactivosapi.presentacion.dto.request.DepartamentosRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.DepartamentosResponseDTO;

@Mapper(componentModel = "spring")
public interface IDepartamentosDtoMapper {

	Departamentos toDomain(DepartamentosRequestDTO dto);

	DepartamentosResponseDTO toResponseDto(Departamentos departamento);

}

