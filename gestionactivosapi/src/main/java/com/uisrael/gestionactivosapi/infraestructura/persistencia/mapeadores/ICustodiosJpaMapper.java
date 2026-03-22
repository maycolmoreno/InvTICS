package com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.gestionactivosapi.dominio.entidades.Custodios;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CustodiosJpa;

@Mapper(componentModel = "spring", uses = {ICargosJpaMapper.class, IUbicacionesJpaMapper.class, IUsuariosJpaMapper.class})
public interface ICustodiosJpaMapper {

    Custodios toDomain(CustodiosJpa entity);

    @Mapping(target = "fkCargo.fkDepartamento", ignore = true)
    CustodiosJpa toEntity(Custodios custodio);
}
