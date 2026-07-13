package com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.gestionactivosapi.dominio.entidades.Ubicaciones;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CustodiosJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.UbicacionesJpa;

/**
 * fkCustodioEncargado se aplana a id/nombre en el dominio (no a un objeto Custodios anidado)
 * a proposito: ICustodiosJpaMapper ya usa este mapper para Custodios.fkUbicacion, y anidar el
 * objeto completo en ambos sentidos crearia un ciclo de mapeo (Ubicacion -> Custodio -> Ubicacion -> ...).
 */
@Mapper(componentModel = "spring", uses = {IDepartamentosJpaMapper.class})
public interface IUbicacionesJpaMapper {

	@Mapping(target = "idCustodioEncargado", source = "fkCustodioEncargado.idCustodio")
	@Mapping(target = "nombreCustodioEncargado", source = "fkCustodioEncargado.nombre")
	Ubicaciones toDomain(UbicacionesJpa entity);

	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	@Mapping(target = "deletedAt", ignore = true)
	@Mapping(target = "fkCustodioEncargado", expression = "java(toCustodioRef(ubicacion.getIdCustodioEncargado()))")
	UbicacionesJpa toEntity(Ubicaciones ubicacion);

	default CustodiosJpa toCustodioRef(Integer idCustodioEncargado) {
		if (idCustodioEncargado == null) {
			return null;
		}
		CustodiosJpa ref = new CustodiosJpa();
		ref.setIdCustodio(idCustodioEncargado);
		return ref;
	}

}
