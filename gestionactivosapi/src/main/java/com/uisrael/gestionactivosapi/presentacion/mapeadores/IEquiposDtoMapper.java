package com.uisrael.gestionactivosapi.presentacion.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.gestionactivosapi.dominio.entidades.CategoriaEquipos;
import com.uisrael.gestionactivosapi.dominio.entidades.Departamentos;
import com.uisrael.gestionactivosapi.dominio.entidades.Equipos;
import com.uisrael.gestionactivosapi.dominio.entidades.Marcas;
import com.uisrael.gestionactivosapi.dominio.entidades.Ubicaciones;
import com.uisrael.gestionactivosapi.presentacion.dto.request.EquiposRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.CategoriaEquiposResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.DepartamentosResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.EquiposResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.MarcasResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.UbicacionesResponseDTO;

@Mapper(componentModel = "spring")
public interface IEquiposDtoMapper {

    // =========================
    // REQUEST DTO -> DOMINIO
    // =========================
    @Mapping(target = "fkCategoria", expression = "java(mapCategoria(dto))")
    @Mapping(target = "fkMarca", expression = "java(mapMarca(dto))")
    @Mapping(target = "fkUbicacion", expression = "java(mapUbicacion(dto))")
    Equipos toDomain(EquiposRequestDTO dto);

    // =========================
    // DOMINIO -> RESPONSE DTO
    // (Aqui esta tu ajuste)
    // =========================
    @Mapping(target = "fkMarca", expression = "java(toMarcaResponse(equipo.getFkMarca()))")
    @Mapping(target = "fkCategoria", expression = "java(toCategoriaResponse(equipo.getFkCategoria()))")
    @Mapping(target = "fkUbicacion", expression = "java(toUbicacionResponse(equipo.getFkUbicacion()))")
    EquiposResponseDTO toResponseDto(Equipos equipo);

    // ==========================================================
    // MAPS MANUALES (REQUEST -> DOMINIO)  (solo ID en relaciones)
    // ==========================================================

    default CategoriaEquipos mapCategoria(EquiposRequestDTO dto) {
        if (dto == null || dto.getFkCategoria() == null) {
			return null;
		}
        return new CategoriaEquipos(
                dto.getFkCategoria().getIdCategoria(),
                null,
                true
        );
    }

    default Marcas mapMarca(EquiposRequestDTO dto) {
        if (dto == null || dto.getFkMarca() == null) {
			return null;
		}
        return new Marcas(
                dto.getFkMarca().getIdMarca(),
                null,
                true
        );
    }

    // ==========================================================
    // MAPS MANUALES (DOMINIO -> RESPONSE) (para que salga NOMBRE)
    // ==========================================================
    default DepartamentosResponseDTO toDepartamentoResponse(Departamentos d) {
        if (d == null) {
			return null;
		}
        DepartamentosResponseDTO r = new DepartamentosResponseDTO();
        r.setIdDepartamento(d.getIdDepartamento());
        r.setNombre(d.getNombre());
        r.setEstado(d.isEstado());
        return r;
    }

    default MarcasResponseDTO toMarcaResponse(Marcas m) {
        if (m == null) {
			return null;
		}
        MarcasResponseDTO r = new MarcasResponseDTO();
        r.setIdMarca(m.getIdMarca());
        r.setNombre(m.getNombre());
        r.setEstado(m.isEstado());
        return r;
    }

    default CategoriaEquiposResponseDTO toCategoriaResponse(CategoriaEquipos c) {
        if (c == null) {
			return null;
		}
        CategoriaEquiposResponseDTO r = new CategoriaEquiposResponseDTO();
        r.setIdCategoria(c.getIdCategoria());
        r.setNombre(c.getNombre());
        r.setEstado(c.isEstado());
        return r;
    }

    default Ubicaciones mapUbicacion(EquiposRequestDTO dto) {
        if (dto == null || dto.getFkUbicacion() == null) {
			return null;
		}
        return new Ubicaciones(
                dto.getFkUbicacion().getIdUbicacion(),
                null, null, true,
                null, null, null, null, null, null, null
        );
    }

    default UbicacionesResponseDTO toUbicacionResponse(Ubicaciones u) {
        if (u == null) {
			return null;
		}
        UbicacionesResponseDTO r = new UbicacionesResponseDTO();
        r.setIdUbicacion(u.getIdUbicacion());
        r.setNombre(u.getNombre());
        r.setAgencia(u.getAgencia());
        r.setEstado(u.isEstado());
        r.setLatitud(u.getLatitud());
        r.setLongitud(u.getLongitud());
        r.setDireccion(u.getDireccion());
        r.setCiudad(u.getCiudad());
        r.setParroquia(u.getParroquia());
        r.setProvincia(u.getProvincia());
        r.setLinkCoordenada(u.getLinkCoordenada());
        return r;
    }
}

