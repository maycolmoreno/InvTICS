package com.uisrael.gestionactivosapi.presentacion.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.gestionactivosapi.dominio.entidades.CategoriaEquipos;
import com.uisrael.gestionactivosapi.presentacion.dto.request.CategoriaEquiposRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.CategoriaEquiposResponseDTO;

@Mapper(componentModel = "spring")
public interface ICategoriaEquiposDtoMapper {

	CategoriaEquipos toDomain(CategoriaEquiposRequestDTO dto);

	CategoriaEquiposResponseDTO toResponseDto(CategoriaEquipos categoriaEquipo);

}

