package com.uisrael.gestionactivosapi.dominio.puertos.repositorios;

import java.util.List;
import java.util.Optional;

import com.uisrael.gestionactivosapi.dominio.entidades.Empresa;

/**
 * Puerto de repositorio para la entidad Empresa.
 * Las implementaciones concretas residen en la capa de infraestructura.
 */
public interface EmpresaRepositorioPuerto {

    Optional<Empresa> buscarPorId(Integer id);

    Optional<Empresa> buscarPorRuc(String ruc);

    List<Empresa> listarActivas();

    List<Empresa> listarTodas();

    Empresa guardar(Empresa empresa);

    boolean existePorRuc(String ruc);

    boolean existePorId(Integer id);
}
