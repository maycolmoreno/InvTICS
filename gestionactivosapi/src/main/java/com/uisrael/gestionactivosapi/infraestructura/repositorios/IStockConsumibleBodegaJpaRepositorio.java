package com.uisrael.gestionactivosapi.infraestructura.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.StockConsumibleBodegaJpa;

public interface IStockConsumibleBodegaJpaRepositorio extends JpaRepository<StockConsumibleBodegaJpa, Integer> {
    Optional<StockConsumibleBodegaJpa> findByBodega_IdBodegaAndConsumible_IdConsumible(
            Integer idBodega, Integer idConsumible);

    List<StockConsumibleBodegaJpa> findByBodega_IdBodega(Integer idBodega);
}
