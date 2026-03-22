package com.uisrael.gestionactivosapi.presentacion.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.gestionactivosapi.dominio.entidades.Marcas;
import com.uisrael.gestionactivosapi.presentacion.dto.request.MarcasRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.MarcasResponseDTO;


@Mapper(componentModel = "spring")
public interface IMarcasDtoMapper {

    default Marcas toDomain(MarcasRequestDTO dto) {
        return Marcas.of(
                dto.getIdMarca(),
                dto.getNombre(),
                true
        );
    }

    MarcasResponseDTO toResponseDto(Marcas marca);
}

