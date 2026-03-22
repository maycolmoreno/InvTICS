package com.uisrael.gestionactivosapi.presentacion.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.gestionactivosapi.dominio.entidades.Roles;
import com.uisrael.gestionactivosapi.presentacion.dto.request.RolesRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.RolesResponseDTO;

@Mapper(componentModel = "spring")
public interface IRolesDtoMapper {

	Roles toDomain(RolesRequestDTO dto);

	RolesResponseDTO toResponseDto(Roles rol);

}

