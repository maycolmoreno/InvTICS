package com.uisrael.gestionactivosapi.infraestructura.persistencia.adaptadores;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import com.uisrael.gestionactivosapi.dominio.dto.EstadisticasEquipoDTO;
import com.uisrael.gestionactivosapi.dominio.dto.HistorialEquipoDTO;
import com.uisrael.gestionactivosapi.dominio.dto.MantenimientoHistorialDTO;
import com.uisrael.gestionactivosapi.dominio.repositorios.IHistorialEquipoRepository;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CustodiasJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.EquiposJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.MantenimientosJpa;

public class HistorialEquipoRepositoryImpl implements IHistorialEquipoRepository {

    private final EntityManager em;

    public HistorialEquipoRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public HistorialEquipoDTO findHistorialByEquipoId(Long equipoId) {
        EquiposJpa equipo = em.createQuery(
                "select e from EquiposJpa e join fetch e.fkMarcas m join fetch e.fkCategoria c where e.idEquipo = :id",
                EquiposJpa.class)
                .setParameter("id", equipoId.intValue())
                .getSingleResult();

        TypedQuery<CustodiasJpa> q = em.createQuery(
                "select ce from CustodiasJpa ce " +
                        "join fetch ce.fkCustodio c " +
                        "left join fetch c.fkCargo cargo " +
                        "left join fetch cargo.fkDepartamento d " +
                        "join fetch ce.fkUbicacion u " +
                        "where ce.fkEquipo.idEquipo = :id " +
                        "and ce.estado = true " +
                        "and ce.fechaFin is null " +
                        "order by ce.fechaInicio desc",
                CustodiasJpa.class);
        q.setParameter("id", equipoId.intValue());
        q.setMaxResults(1);
        CustodiasJpa activo = q.getResultList().stream().findFirst().orElse(null);

        HistorialEquipoDTO dto = new HistorialEquipoDTO();
        dto.setIdEquipo(equipo.getIdEquipo());
        dto.setMarca(equipo.getFkMarcas() != null ? equipo.getFkMarcas().getNombre() : "");
        dto.setModelo(equipo.getModelo());
        dto.setSerial(equipo.getSerial());
        dto.setTipoEquipo(equipo.getTipoEquipo());
        dto.setCodigoSap(equipo.getCodigoSap());
        dto.setFechaCompra(equipo.getFechaCompra());
        dto.setEstadoEquipo(equipo.getEstadoEquipo());
        dto.setSistemaOperativo(equipo.getSistemaOperativo());
        dto.setProcesador(equipo.getProcesador());
        dto.setMemoriaRamGb(equipo.getMemoriaRamGb());
        dto.setCapacidadAlmacenamientoGb(equipo.getCapacidadAlmacenamientoGb());
        dto.setLicenciaWindowsActivada(equipo.getLicenciaWindowsActivada());
        dto.setUnionDominio(equipo.getUnionDominio());
        dto.setCategoriaNombre(equipo.getFkCategoria() != null ? equipo.getFkCategoria().getNombre() : "");

        if (activo != null) {
            dto.setCustodioNombre(activo.getFkCustodio() != null ? activo.getFkCustodio().getNombre() : "");
            if (activo.getFkCustodio() != null
                    && activo.getFkCustodio().getFkCargo() != null
                    && activo.getFkCustodio().getFkCargo().getFkDepartamento() != null) {
                dto.setDepartamentoNombre(activo.getFkCustodio().getFkCargo().getFkDepartamento().getNombre());
            }
            if (activo.getFkUbicacion() != null) {
                dto.setUbicacionNombre(activo.getFkUbicacion().getNombre());
                dto.setUbicacionCiudad(activo.getFkUbicacion().getCiudad());
            }
            dto.setFechaInicioCustodio(activo.getFechaInicio());
        }

        return dto;
    }

    @Override
    public List<MantenimientoHistorialDTO> findMantenimientosByEquipoId(Long equipoId) {
        List<MantenimientosJpa> mantenimientos = em.createQuery(
                "select m from MantenimientosJpa m " +
                        "left join fetch m.fkUsuario u " +
                        "where m.equipoId = :id " +
                        "order by case when m.fecCierre is null then 1 else 0 end, m.fecCierre desc",
                MantenimientosJpa.class)
                .setParameter("id", equipoId)
                .getResultList();

        return mantenimientos.stream().map(m -> {
            MantenimientoHistorialDTO dto = new MantenimientoHistorialDTO();
            dto.setIdMantenimiento(m.getIdMantenimiento());
            dto.setSineSnapshoted(m.getSineSnapshoted());
            dto.setEstadoInterno(m.getEstadoInterno() != null ? m.getEstadoInterno().name() : null);
            dto.setDescripcion(m.getDescripcion());
            dto.setFechaCierre(m.getFecCierre());
            dto.setTecnicoNombre(m.getFkUsuario() != null ? m.getFkUsuario().getNombre() : "");
            dto.setTipoInferido(inferirTipo(m.getDescripcion()));
            return dto;
        }).toList();
    }

    @Override
    public EstadisticasEquipoDTO calcularEstadisticas(Long equipoId) {
        List<MantenimientosJpa> mantenimientos = em.createQuery(
                "select m from MantenimientosJpa m where m.equipoId = :id",
                MantenimientosJpa.class)
                .setParameter("id", equipoId)
                .getResultList();

        EstadisticasEquipoDTO stats = new EstadisticasEquipoDTO();
        stats.setTotalMantenimientos(mantenimientos.size());

        int cerrados = (int) mantenimientos.stream()
                .filter(m -> m.getEstadoInterno() != null && "CERRADO".equalsIgnoreCase(m.getEstadoInterno().name()))
                .count();
        int enProceso = (int) mantenimientos.stream()
                .filter(m -> m.getEstadoInterno() != null && "EN_PROCESO".equalsIgnoreCase(m.getEstadoInterno().name()))
                .count();
        stats.setTotalCerrados(cerrados);
        stats.setTotalEnProceso(enProceso);

        LocalDateTime ultimo = mantenimientos.stream()
                .map(MantenimientosJpa::getFecCierre)
                .filter(f -> f != null)
                .max(Comparator.naturalOrder())
                .orElse(null);
        if (ultimo != null) {
            stats.setDiasSinMantenimiento(ChronoUnit.DAYS.between(ultimo.toLocalDate(), LocalDate.now()));
        }

        List<LocalDateTime> fechas = mantenimientos.stream()
                .map(MantenimientosJpa::getFecCierre)
                .filter(f -> f != null)
                .sorted()
                .toList();
        if (fechas.size() >= 2) {
            long sum = 0;
            for (int i = 1; i < fechas.size(); i++) {
                sum += ChronoUnit.DAYS.between(fechas.get(i - 1).toLocalDate(), fechas.get(i).toLocalDate());
            }
            stats.setPromedioDiasEntreMantenimientos(sum / (double) (fechas.size() - 1));
        }

        Map<Integer, Long> porAnio = new LinkedHashMap<>();
        mantenimientos.stream()
                .filter(m -> m.getFecCierre() != null || m.getCreadoEn() != null)
                .forEach(m -> {
                    int year = (m.getFecCierre() != null ? m.getFecCierre().getYear()
                            : m.getCreadoEn().getYear());
                    porAnio.put(year, porAnio.getOrDefault(year, 0L) + 1);
                });
        stats.setMantsPorAnio(porAnio);

        return stats;
    }

    private String inferirTipo(String descripcion) {
        if (descripcion == null) {
            return "N/A";
        }
        String d = descripcion.toLowerCase();
        if (d.contains("preventivo")) {
            return "PREVENTIVO";
        }
        if (d.contains("correctivo")) {
            return "CORRECTIVO";
        }
        return "N/A";
    }
}
