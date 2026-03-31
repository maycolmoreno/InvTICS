package com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.gestionactivosapi.dominio.entidades.Custodios;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CustodiosJpa;

@Mapper(componentModel = "spring", uses = {ICargosJpaMapper.class, IUbicacionesJpaMapper.class, IUsuariosJpaMapper.class})
public interface ICustodiosJpaMapper {

    Custodios toDomain(CustodiosJpa entity);

    @Mapping(target = "fkCargo.fkDepartamento", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    CustodiosJpa toEntity(Custodios custodio);
}
