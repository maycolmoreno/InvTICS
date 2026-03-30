package com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.uisrael.gestionactivosapi.dominio.entidades.FirmaMantenimiento;
import com.uisrael.gestionactivosapi.dominio.entidades.TipoFirma;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.FirmaMantenimientoRepositorioPuerto;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.FirmaMantenimientoJpa;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IFirmaMantenimientoJpaRepositorio;

public class FirmaMantenimientoRepositorioImpl implements FirmaMantenimientoRepositorioPuerto {

	private final IFirmaMantenimientoJpaRepositorio jpaRepositorio;

	public FirmaMantenimientoRepositorioImpl(IFirmaMantenimientoJpaRepositorio jpaRepositorio) {
		this.jpaRepositorio = jpaRepositorio;
	}

	@Override
	public FirmaMantenimiento guardar(FirmaMantenimiento firmaMantenimiento) {
		return toDomain(jpaRepositorio.save(toEntity(firmaMantenimiento)));
	}

	@Override
	public List<FirmaMantenimiento> listarPorMantenimiento(Integer idMantenimiento) {
		return jpaRepositorio.findByIdMantenimiento(idMantenimiento).stream().map(this::toDomain)
				.collect(Collectors.toList());
	}

	@Override
	public Optional<FirmaMantenimiento> buscarPorMantenimientoYTipo(Integer idMantenimiento, TipoFirma tipoFirma) {
		return jpaRepositorio.findByIdMantenimientoAndTipoFirma(idMantenimiento, tipoFirma).map(this::toDomain);
	}

	private FirmaMantenimiento toDomain(FirmaMantenimientoJpa entity) {
		return new FirmaMantenimiento(entity.getId(), entity.getIdMantenimiento(), entity.getTipoFirma(),
				entity.getFirmaBase64(), entity.getFirmadoEn(), entity.getIpOrigen());
	}

	private FirmaMantenimientoJpa toEntity(FirmaMantenimiento firma) {
		FirmaMantenimientoJpa entity = new FirmaMantenimientoJpa();
		entity.setId(firma.id());
		entity.setIdMantenimiento(firma.idMantenimiento());
		entity.setTipoFirma(firma.tipoFirma());
		entity.setFirmaBase64(firma.firmaBase64());
		entity.setFirmadoEn(firma.firmadoEn());
		entity.setIpOrigen(firma.ipOrigen());
		return entity;
	}
}
