package com.uisrael.gestionactivosapi.presentacion.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.gestionactivosapi.dominio.entidades.Custodios;
import com.uisrael.gestionactivosapi.dominio.entidades.Departamentos;
import com.uisrael.gestionactivosapi.presentacion.dto.request.CustodiosRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.DepartamentosResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.CustodiosResponseDTO;

@Mapper(componentModel = "spring")
public interface ICustodiosDtoMapper {

    Custodios toDomain(CustodiosRequestDTO dto);

    @Mapping(target = "fkDepartamento", expression = "java(toDepartamentoResponse(custodio.getFkDepartamento()))")
    CustodiosResponseDTO toResponseDto(Custodios custodio);

    default DepartamentosResponseDTO toDepartamentoResponse(Departamentos departamento) {
        if (departamento == null) {
            return null;
        }
        DepartamentosResponseDTO dto = new DepartamentosResponseDTO();
        dto.setIdDepartamento(departamento.getIdDepartamento());
        dto.setNombre(departamento.getNombre());
        dto.setEstado(departamento.isEstado());
        return dto;
    }
}

