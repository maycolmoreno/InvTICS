package com.uisrael.gestionactivosapi.presentacion.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.gestionactivosapi.dominio.entidades.Usuarios;
import com.uisrael.gestionactivosapi.presentacion.dto.request.UsuariosRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.UsuariosResponseDTO;

@Mapper(componentModel = "spring")
public interface IUsuariosDtoMapper {

	Usuarios toDomain(UsuariosRequestDTO dto);

	@Mapping(target = "fkRol", source = "fkRol")
	@Mapping(target = "fkDepartamento", source = "fkDepartamento")
	UsuariosResponseDTO toResponseDto(Usuarios usuario);

}

