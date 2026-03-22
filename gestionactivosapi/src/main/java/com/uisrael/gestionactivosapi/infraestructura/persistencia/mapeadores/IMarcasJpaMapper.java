package com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.gestionactivosapi.dominio.entidades.Marcas;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.MarcasJpa;

@Mapper(componentModel = "spring")
public interface IMarcasJpaMapper {

    default Marcas toDomain(MarcasJpa entity) {
        if (entity == null) {
			return null;
		}

        return Marcas.of(
                entity.getIdMarca(),
                entity.getNombre(),
                entity.isEstado()
        );
    }


    MarcasJpa toEntity(Marcas marca);
}