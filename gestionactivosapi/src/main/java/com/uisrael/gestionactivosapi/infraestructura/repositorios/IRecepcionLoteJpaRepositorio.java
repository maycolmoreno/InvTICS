package com.uisrael.gestionactivosapi.infraestructura.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.RecepcionLoteJpa;

public interface IRecepcionLoteJpaRepositorio extends JpaRepository<RecepcionLoteJpa, Integer> {
    List<RecepcionLoteJpa> findByOrdenCompra_IdOrdenCompra(Integer idOrdenCompra);
    List<RecepcionLoteJpa> findByOrdenCompraDetalle_IdOrdenCompraDetalle(Integer idOrdenCompraDetalle);
}
