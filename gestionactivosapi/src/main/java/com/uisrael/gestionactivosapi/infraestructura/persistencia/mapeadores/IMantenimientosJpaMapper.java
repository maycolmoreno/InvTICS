package com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.gestionactivosapi.dominio.entidades.EquipoSnapshot;
import com.uisrael.gestionactivosapi.dominio.entidades.Mantenimientos;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.EquipoSnapshotEmbeddable;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.MantenimientosJpa;

@Mapper(componentModel = "spring")
public interface IMantenimientosJpaMapper {

    Mantenimientos toDomain(MantenimientosJpa entity);

    MantenimientosJpa toEntity(Mantenimientos mantenimiento);

    default EquipoSnapshot toDomain(EquipoSnapshotEmbeddable snapshot) {
        if (snapshot == null) {
            return null;
        }
        return new EquipoSnapshot(snapshot.getSerieSnapshot(), snapshot.getSineSnapshot(), snapshot.getYearSnapshoted());
    }

    default EquipoSnapshotEmbeddable toEntity(EquipoSnapshot snapshot) {
        if (snapshot == null) {
            return null;
        }
        EquipoSnapshotEmbeddable entity = new EquipoSnapshotEmbeddable();
        entity.setSerieSnapshot(snapshot.serieSnapshot());
        entity.setSineSnapshot(snapshot.sineSnapshot());
        entity.setYearSnapshoted(snapshot.yearSnapshoted());
        return entity;
    }
}
