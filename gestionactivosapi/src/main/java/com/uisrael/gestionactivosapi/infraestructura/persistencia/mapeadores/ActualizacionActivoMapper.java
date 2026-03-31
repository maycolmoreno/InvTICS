package com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.ActualizacionActivoJpa;
import com.uisrael.gestionactivosapi.presentacion.dto.response.ActualizacionActivoResponseDTO;

/**
 * MapStruct mapper para convertir ActualizacionActivoJpa → ActualizacionActivoResponseDTO.
 * Resuelve el nombre del usuario a través de la relación @ManyToOne usuarioRel,
 * eliminando la necesidad del campo varchar desnormalizado.
 */
@Mapper(componentModel = "spring")
public interface ActualizacionActivoMapper {

    @Mapping(source = "fkUsuarioActualizacion", target = "usuarioId")
    @Mapping(source = "usuarioRel.nombre", target = "usuarioNombre")
    @Mapping(source = "fechaActualizacion", target = "fechaActualizacion")
    ActualizacionActivoResponseDTO toDTO(ActualizacionActivoJpa entity);
}
