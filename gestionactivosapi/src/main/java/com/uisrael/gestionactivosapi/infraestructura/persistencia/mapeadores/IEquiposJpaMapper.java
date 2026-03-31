package com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.gestionactivosapi.dominio.entidades.CategoriaEquipos;
import com.uisrael.gestionactivosapi.dominio.entidades.Equipos;
import com.uisrael.gestionactivosapi.dominio.entidades.Marcas;
import com.uisrael.gestionactivosapi.dominio.entidades.Ubicaciones;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CategoriaEquiposJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.EquiposJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.MarcasJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.UbicacionesJpa;

@Mapper(componentModel = "spring")
public interface IEquiposJpaMapper {


	@Mapping(source = "fkMarca", target = "fkMarcas")
	@Mapping(source = "fkCategoria", target = "fkCategoria")
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	@Mapping(target = "deletedAt", ignore = true)
	@Mapping(target = "activoFijo", ignore = true)
	EquiposJpa toEntity(Equipos equipo);



	@Mapping(source = "fkMarcas", target = "fkMarca")
	@Mapping(source = "fkCategoria", target = "fkCategoria")
	@Mapping(target = "fkUbicacion", ignore = true)
	Equipos toDomain(EquiposJpa entity);



    default MarcasJpa map(Marcas m) {
        if (m == null) {
			return null;
		}
        MarcasJpa j = new MarcasJpa();
        j.setIdMarca(m.getIdMarca());
        return j;
    }

    default Marcas map(MarcasJpa j) {
        if (j == null) {
			return null;
		}

        return new Marcas(j.getIdMarca(), j.getNombre(), j.isEstado());
    }

    default CategoriaEquiposJpa map(CategoriaEquipos c) {
        if (c == null) {
			return null;
		}
        CategoriaEquiposJpa j = new CategoriaEquiposJpa();
        j.setIdCategoria(c.getIdCategoria());
        return j;
    }

    default CategoriaEquipos map(CategoriaEquiposJpa j) {
        if (j == null) {
			return null;
		}
        return new CategoriaEquipos(
            j.getIdCategoria(),
            j.getNombre(),
            j.isEstado()
        );
    }

    default UbicacionesJpa map(Ubicaciones u) {
        if (u == null) {
			return null;
		}
        UbicacionesJpa j = new UbicacionesJpa();
        j.setIdUbicacion(u.getIdUbicacion());
        return j;
    }

    default Ubicaciones map(UbicacionesJpa j) {
        if (j == null) {
			return null;
		}
        return new Ubicaciones(
            j.getIdUbicacion(),
            j.getNombre(),
            j.getAgencia(),
            j.isEstado(),
            j.getLatitud(),
            j.getLongitud(),
            j.getDireccion(),
            j.getCiudad(),
            j.getParroquia(),
            j.getProvincia(),
            j.getLinkCoordenada()
        );
    }
}
