package com.uisrael.gestionactivosapi.dominio.puertos.repositorios;

import java.util.List;

import com.uisrael.gestionactivosapi.dominio.dto.EstadisticasEquipoDTO;
import com.uisrael.gestionactivosapi.dominio.dto.HistorialEquipoDTO;
import com.uisrael.gestionactivosapi.dominio.dto.MantenimientoHistorialDTO;

public interface HistorialEquipoRepositorioPuerto {

    HistorialEquipoDTO findHistorialByEquipoId(Long equipoId);

    List<MantenimientoHistorialDTO> findMantenimientosByEquipoId(Long equipoId);

    EstadisticasEquipoDTO calcularEstadisticas(Long equipoId);
}
