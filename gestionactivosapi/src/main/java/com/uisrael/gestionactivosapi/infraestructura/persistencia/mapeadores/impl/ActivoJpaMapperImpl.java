package com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.impl;

import com.uisrael.gestionactivosapi.dominio.entidades.Activo;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.ActivoJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.IActivoJpaMapper;
import org.springframework.stereotype.Component;

@Component
public class ActivoJpaMapperImpl implements IActivoJpaMapper {

	@Override
	public ActivoJpa toPersistence(Activo dominio) {
		if (dominio == null) {
			return null;
		}
		
		ActivoJpa jpa = new ActivoJpa();
		jpa.setIdActivo(dominio.getIdActivo());
		jpa.setNombre(dominio.getNombre());
		jpa.setDescripcion(dominio.getDescripcion());
		jpa.setSerie(dominio.getSerie());
		jpa.setModelo(dominio.getModelo());
		jpa.setFechaAdquisicion(dominio.getFechaAdquisicion());
		jpa.setValorActual(dominio.getValorActual());
		jpa.setEstado(dominio.getEstado());
		jpa.setUbicacion(dominio.getUbicacion());
		jpa.setFkDepartamento(dominio.getFkDepartamento());
		jpa.setFkCategoria(dominio.getFkCategoria());
		
		return jpa;
	}

	@Override
	public Activo toDomain(ActivoJpa persistencia) {
		if (persistencia == null) {
			return null;
		}
		
		Activo dominio = new Activo();
		dominio.setIdActivo(persistencia.getIdActivo());
		dominio.setNombre(persistencia.getNombre());
		dominio.setDescripcion(persistencia.getDescripcion());
		dominio.setSerie(persistencia.getSerie());
		dominio.setModelo(persistencia.getModelo());
		dominio.setFechaAdquisicion(persistencia.getFechaAdquisicion());
		dominio.setValorActual(persistencia.getValorActual());
		dominio.setEstado(persistencia.getEstado());
		dominio.setUbicacion(persistencia.getUbicacion());
		dominio.setFkDepartamento(persistencia.getFkDepartamento() != null ? persistencia.getFkDepartamento() : 0);
		dominio.setFkCategoria(persistencia.getFkCategoria() != null ? persistencia.getFkCategoria() : 0);
		
		return dominio;
	}
}
