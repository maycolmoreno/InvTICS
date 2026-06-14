package com.uisrael.gestionactivosapi.infraestructura.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.gestionactivosapi.dominio.entidades.inventario.EstadoOrdenCompraDetalle;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.OrdenCompraDetalleJpa;

public interface IOrdenCompraDetalleJpaRepositorio extends JpaRepository<OrdenCompraDetalleJpa, Integer> {
    List<OrdenCompraDetalleJpa> findByOrdenCompra_IdOrdenCompra(Integer idOrdenCompra);
    List<OrdenCompraDetalleJpa> findByOrdenCompra_IdOrdenCompraAndEstado(Integer idOrdenCompra, EstadoOrdenCompraDetalle estado);
}
