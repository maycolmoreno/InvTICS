package com.uisrael.consumogestionactivosapi.service;

import java.util.List;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.BodegaRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.AsignacionActivoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.AsignacionConsumibleRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.BajaActivoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.ConsumibleRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.DevolucionActivoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.DevolucionConsumibleRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.OrdenCompraRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.RecepcionActivoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.RecepcionConsumibleRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.TrasladoActivoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.TrasladoConsumibleRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.ActivoInventarioResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.BodegaResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.ConsumibleResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.MovimientoInventarioResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.OrdenCompraResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.StockConsumibleResponseDTO;

public interface IInventarioOperacionServicio {
    List<BodegaResponseDTO> listarBodegas();
    BodegaResponseDTO crearBodega(BodegaRequestDTO request);
    List<ConsumibleResponseDTO> listarConsumibles();
    ConsumibleResponseDTO crearConsumible(ConsumibleRequestDTO request);
    List<OrdenCompraResponseDTO> listarOrdenesCompra();
    OrdenCompraResponseDTO crearOrdenCompra(OrdenCompraRequestDTO request);
    OrdenCompraResponseDTO confirmarRecepcionOrden(Integer ordenCompraId);
    ActivoInventarioResponseDTO recibirActivo(RecepcionActivoRequestDTO request);
    StockConsumibleResponseDTO recibirConsumible(RecepcionConsumibleRequestDTO request);
    List<StockConsumibleResponseDTO> listarStockPorBodega(Integer bodegaId);
    List<MovimientoInventarioResponseDTO> listarMovimientosRecientes();
    List<ActivoInventarioResponseDTO> listarActivosEnBodega();
    ActivoInventarioResponseDTO asignarActivo(AsignacionActivoRequestDTO request);
    StockConsumibleResponseDTO asignarConsumible(AsignacionConsumibleRequestDTO request);
    ActivoInventarioResponseDTO devolverActivo(DevolucionActivoRequestDTO request);
    StockConsumibleResponseDTO devolverConsumible(DevolucionConsumibleRequestDTO request);
    ActivoInventarioResponseDTO trasladarActivo(TrasladoActivoRequestDTO request);
    StockConsumibleResponseDTO trasladarConsumible(TrasladoConsumibleRequestDTO request);
    ActivoInventarioResponseDTO darBajaActivo(BajaActivoRequestDTO request);
}
