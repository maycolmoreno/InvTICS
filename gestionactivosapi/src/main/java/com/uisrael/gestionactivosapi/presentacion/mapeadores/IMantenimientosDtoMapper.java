package com.uisrael.gestionactivosapi.presentacion.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.gestionactivosapi.dominio.entidades.Mantenimientos;
import com.uisrael.gestionactivosapi.presentacion.dto.request.MantenimientosRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.MantenimientosResponseDTO;

@Mapper(componentModel = "spring")
public interface IMantenimientosDtoMapper {

    @Mapping(target = "idMantenimiento", source = "id")
    Mantenimientos toDomain(MantenimientosRequestDTO dto);

    MantenimientosResponseDTO toResponseDto(Mantenimientos mantenimiento);
}
