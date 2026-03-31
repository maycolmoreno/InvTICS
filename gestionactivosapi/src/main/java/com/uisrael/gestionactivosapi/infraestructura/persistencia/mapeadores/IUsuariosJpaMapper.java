package com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.gestionactivosapi.dominio.entidades.Usuarios;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.UsuariosJpa;

@Mapper(componentModel = "spring", uses = {IDepartamentosJpaMapper.class, IRolesJpaMapper.class})
public interface IUsuariosJpaMapper {

	@Mapping(source = "idUsuario", target = "idUsuario")
	@Mapping(source = "fkDepartamento", target = "fkDepartamento")
	@Mapping(source = "fkRol", target = "fkRol")
	Usuarios toDomain(UsuariosJpa entity);

	@Mapping(source = "idUsuario", target = "idUsuario")
	@Mapping(source = "fkDepartamento", target = "fkDepartamento")
	@Mapping(source = "fkRol", target = "fkRol")
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	@Mapping(target = "deletedAt", ignore = true)
	UsuariosJpa toEntity(Usuarios usuario);

}
