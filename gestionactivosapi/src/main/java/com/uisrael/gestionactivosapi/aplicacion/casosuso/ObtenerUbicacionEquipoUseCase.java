package com.uisrael.gestionactivosapi.aplicacion.casosuso;

import java.util.Optional;

import com.uisrael.gestionactivosapi.dominio.excepciones.EquipoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.valoresobjeto.UbicacionActiva;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CustodiasJpa;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.ICustodiasJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IEquiposJpaRepositorio;

import lombok.RequiredArgsConstructor;

/**
 * Caso de uso: Obtener la ubicación actual de un equipo.
 *
 * <p>La ubicación real de un equipo se determina por su custodia activa
 * (estado = true AND fecha_fin IS NULL), no por un campo directo en equipos.
 * Esto resuelve la contradicción entre equipos.fk_ubicacion y custodias.fk_ubicacion.</p>
 */
@RequiredArgsConstructor
public class ObtenerUbicacionEquipoUseCase {

    private final IEquiposJpaRepositorio equipoRepo;
    private final ICustodiasJpaRepositorio custodiaRepo;

    /**
     * Obtiene la ubicación actual del equipo a partir de su custodia activa.
     *
     * @param idEquipo ID del equipo
     * @return la ubicación activa, o vacío si el equipo no tiene custodia activa
     * @throws EquipoNoEncontradoException si el equipo no existe
     */
    public Optional<UbicacionActiva> ejecutar(Integer idEquipo) {
        if (!equipoRepo.existsById(idEquipo)) {
            throw new EquipoNoEncontradoException(idEquipo);
        }

        return custodiaRepo
                .findFirstByFkEquipo_IdEquipoAndEstadoTrueAndFechaFinIsNullOrderByIdCustodiaEquipoDesc(idEquipo)
                .map(this::toUbicacionActiva);
    }

    private UbicacionActiva toUbicacionActiva(CustodiasJpa custodia) {
        var custodio = custodia.getFkCustodio();
        var ubicacion = custodio != null ? custodio.getFkUbicacion() : null;

        return new UbicacionActiva(
            ubicacion != null ? ubicacion.getIdUbicacion() : null,
            ubicacion != null ? ubicacion.getNombre() : null,
            ubicacion != null ? ubicacion.getAgencia() : null,
            custodio != null ? custodio.getNombre() : null,
            custodia.getIdCustodiaEquipo()
        );
    }
}
