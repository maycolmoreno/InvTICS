package com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.gestionactivosapi.dominio.entidades.Custodias;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CustodiasJpa;

@Mapper(componentModel = "spring", uses = {IEquiposJpaMapper.class, ICustodiosJpaMapper.class, IUbicacionesJpaMapper.class})
public interface ICustodiasJpaMapper {

    Custodias toDomain(CustodiasJpa entity);

    CustodiasJpa toEntity(Custodias custodia);
}
