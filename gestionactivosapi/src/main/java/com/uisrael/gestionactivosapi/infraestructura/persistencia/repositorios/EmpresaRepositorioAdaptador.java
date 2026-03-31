package com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios;

import java.util.List;
import java.util.Optional;

import com.uisrael.gestionactivosapi.dominio.entidades.Empresa;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.EmpresaRepositorioPuerto;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.EmpresaJpa;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IEmpresaJpaRepositorio;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EmpresaRepositorioAdaptador implements EmpresaRepositorioPuerto {

    private final IEmpresaJpaRepositorio jpaRepo;

    @Override
    public Optional<Empresa> buscarPorId(Integer id) {
        return jpaRepo.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Empresa> buscarPorRuc(String ruc) {
        return jpaRepo.findByRuc(ruc).map(this::toDomain);
    }

    @Override
    public List<Empresa> listarActivas() {
        return jpaRepo.findByEstadoTrue().stream()
                .map(this::toDomain).toList();
    }

    @Override
    public List<Empresa> listarTodas() {
        return jpaRepo.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Empresa guardar(Empresa empresa) {
        EmpresaJpa jpa = toJpa(empresa);
        return toDomain(jpaRepo.save(jpa));
    }

    @Override
    public boolean existePorRuc(String ruc) {
        return jpaRepo.existsByRucIgnoreCase(ruc);
    }

    @Override
    public boolean existePorId(Integer id) {
        return jpaRepo.existsById(id);
    }

    private Empresa toDomain(EmpresaJpa jpa) {
        Empresa e = new Empresa();
        e.setIdEmpresa(jpa.getIdEmpresa());
        e.setNombre(jpa.getNombre());
        e.setRuc(jpa.getRuc());
        e.setDireccion(jpa.getDireccion());
        e.setTelefono(jpa.getTelefono());
        e.setCorreo(jpa.getCorreo());
        e.setEstado(jpa.isEstado());
        e.setCreadoEn(jpa.getCreatedAt());
        e.setActualizadoEn(jpa.getUpdatedAt());
        e.setEliminadoEn(jpa.getDeletedAt());
        return e;
    }

    private EmpresaJpa toJpa(Empresa e) {
        EmpresaJpa jpa = new EmpresaJpa();
        jpa.setIdEmpresa(e.getIdEmpresa());
        jpa.setNombre(e.getNombre());
        jpa.setRuc(e.getRuc());
        jpa.setDireccion(e.getDireccion());
        jpa.setTelefono(e.getTelefono());
        jpa.setCorreo(e.getCorreo());
        jpa.setEstado(e.isEstado());
        return jpa;
    }
}
