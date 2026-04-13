package com.uisrael.gestionactivosapi.presentacion.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.gestionactivosapi.dominio.entidades.Mantenimientos;
import com.uisrael.gestionactivosapi.presentacion.dto.request.MantenimientosRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.MantenimientosResponseDTO;

@Mapper(componentModel = "spring")
public interface IMantenimientosDtoMapper {

    @Mapping(target = "idMantenimiento", source = "id")
    @Mapping(target = "equipoSnapshot", ignore = true)
    @Mapping(target = "sineSnapshot", ignore = true)
    @Mapping(target = "estadoGeneral", ignore = true)
    @Mapping(target = "proximaFecha", ignore = true)
    @Mapping(target = "activo", ignore = true)
    Mantenimientos toDomain(MantenimientosRequestDTO dto);

    MantenimientosResponseDTO toResponseDto(Mantenimientos mantenimiento);
}
