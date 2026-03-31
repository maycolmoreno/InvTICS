package com.uisrael.gestionactivosapi.presentacion.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.gestionactivosapi.dominio.entidades.Ubicaciones;
import com.uisrael.gestionactivosapi.presentacion.dto.request.UbicacionesRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.UbicacionesResponseDTO;

@Mapper(componentModel = "spring", uses = {IDepartamentosDtoMapper.class})
public interface IUbicacionesDtoMapper {

	Ubicaciones toDomain(UbicacionesRequestDTO dto);

	UbicacionesResponseDTO toResponseDto(Ubicaciones ubicacion);

}

