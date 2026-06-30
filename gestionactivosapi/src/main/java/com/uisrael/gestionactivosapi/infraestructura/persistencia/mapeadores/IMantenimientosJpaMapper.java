package com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.gestionactivosapi.dominio.entidades.EquipoSnapshot;
import com.uisrael.gestionactivosapi.dominio.entidades.Mantenimientos;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.EquipoSnapshotEmbeddable;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.MantenimientosJpa;

@Mapper(componentModel = "spring")
public interface IMantenimientosJpaMapper {

    @Mapping(target = "frecuenciaDias", ignore = true)
    Mantenimientos toDomain(MantenimientosJpa entity);

    @Mapping(target = "codigoInternoSnapshot", ignore = true)
    @Mapping(target = "fkEquipo", ignore = true)
    @Mapping(target = "fkCliente", ignore = true)
    @Mapping(target = "fkUsuario", ignore = true)
    @Mapping(target = "programadoRel", ignore = true)
    @Mapping(target = "equipos", ignore = true)
    @Mapping(target = "resultadoTecnico", ignore = true)
    @Mapping(target = "cerradoPor", ignore = true)
    MantenimientosJpa toEntity(Mantenimientos mantenimiento);

    default EquipoSnapshot toDomain(EquipoSnapshotEmbeddable snapshot) {
        if (snapshot == null) {
            return null;
        }
        return new EquipoSnapshot(snapshot.getSerieSnapshot(), snapshot.getCodigoInternoSnapshot(), snapshot.getYearSnapshoted());
    }

    default EquipoSnapshotEmbeddable toEntity(EquipoSnapshot snapshot) {
        if (snapshot == null) {
            return null;
        }
        EquipoSnapshotEmbeddable entity = new EquipoSnapshotEmbeddable();
        entity.setSerieSnapshot(snapshot.serieSnapshot());
        entity.setCodigoInternoSnapshot(snapshot.sineSnapshot());
        entity.setYearSnapshoted(snapshot.yearSnapshoted());
        return entity;
    }
}
