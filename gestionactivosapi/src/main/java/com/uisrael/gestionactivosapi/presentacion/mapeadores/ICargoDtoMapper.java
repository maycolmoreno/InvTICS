package com.uisrael.gestionactivosapi.presentacion.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.gestionactivosapi.dominio.entidades.Cargos;
import com.uisrael.gestionactivosapi.presentacion.dto.request.CargosRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.CargosResponseDTO;

@Mapper(componentModel = "spring", uses = {IDepartamentosDtoMapper.class})
public interface ICargoDtoMapper {

	Cargos toDomain(CargosRequestDTO dto);

	CargosResponseDTO toResponseDto(Cargos cargo);
}

