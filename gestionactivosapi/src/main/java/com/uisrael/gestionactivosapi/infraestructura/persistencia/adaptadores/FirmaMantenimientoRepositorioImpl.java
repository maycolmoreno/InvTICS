package com.uisrael.gestionactivosapi.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;

import com.uisrael.gestionactivosapi.dominio.entidades.FirmaMantenimiento;
import com.uisrael.gestionactivosapi.dominio.entidades.TipoFirma;
import com.uisrael.gestionactivosapi.dominio.repositorios.IFirmaMantenimientoRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.FirmaMantenimientoJpa;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IFirmaMantenimientoJpaRepositorio;

public class FirmaMantenimientoRepositorioImpl implements IFirmaMantenimientoRepositorio {

    private final IFirmaMantenimientoJpaRepositorio jpaRepositorio;

    public FirmaMantenimientoRepositorioImpl(IFirmaMantenimientoJpaRepositorio jpaRepositorio) {
        this.jpaRepositorio = jpaRepositorio;
    }

    @Override
    public FirmaMantenimiento guardar(FirmaMantenimiento firmaMantenimiento) {
        FirmaMantenimientoJpa entity = new FirmaMantenimientoJpa();
        entity.setId(firmaMantenimiento.id());
        entity.setIdMantenimiento(firmaMantenimiento.idMantenimiento());
        entity.setTipoFirma(firmaMantenimiento.tipoFirma());
        entity.setFirmaBase64(firmaMantenimiento.firmaBase64());
        entity.setFirmadoEn(firmaMantenimiento.firmadoEn());
        entity.setIpOrigen(firmaMantenimiento.ipOrigen());
        return toDomain(jpaRepositorio.save(entity));
    }

    @Override
    public List<FirmaMantenimiento> listarPorMantenimiento(Integer idMantenimiento) {
        return jpaRepositorio.findByIdMantenimiento(idMantenimiento).stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<FirmaMantenimiento> buscarPorMantenimientoYTipo(Integer idMantenimiento, TipoFirma tipoFirma) {
        return jpaRepositorio.findByIdMantenimientoAndTipoFirma(idMantenimiento, tipoFirma).map(this::toDomain);
    }

    private FirmaMantenimiento toDomain(FirmaMantenimientoJpa entity) {
        return new FirmaMantenimiento(
                entity.getId(),
                entity.getIdMantenimiento(),
                entity.getTipoFirma(),
                entity.getFirmaBase64(),
                entity.getFirmadoEn(),
                entity.getIpOrigen());
    }
}
