package com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.uisrael.gestionactivosapi.dominio.entidades.EstadoInternoMantenimiento;
import com.uisrael.gestionactivosapi.dominio.entidades.TipoOrigenMantenimiento;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "mantenimientos")
public class MantenimientosJpa implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mantenimiento")
    private Integer idMantenimiento;

    @Column(name = "equipo_id")
    private Integer equipoId;

    @OneToMany(mappedBy = "mantenimiento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MantenimientoEquipoJpa> equipos = new ArrayList<>();

    @Embedded
    private EquipoSnapshotEmbeddable equipoSnapshot = new EquipoSnapshotEmbeddable();

    @Column(name = "cliente_id")
    private Integer idCliente;

    @Column(name = "empresa_id")
    private Integer empresaId;

    @Column(name = "fecha_programada", nullable = false)
    private LocalDateTime fechaProgramada;

    @Column(name = "fec_cierre")
    private LocalDateTime fecCierre;

    @Column(name = "descripcion", length = 2000)
    private String descripcion;

    @Column(name = "tipo_mantenimiento", length = 30)
    private String tipoMantenimiento;

    @Column(name = "tecnico_id")
    private Integer idUsuario;

    @Column(name = "estado", length = 30)
    private String estado;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_interno", length = 30)
    private EstadoInternoMantenimiento estadoInterno;

    @Column(name = "estado_general", length = 20)
    private String estadoGeneral;

    @Column(name = "proxima_fecha")
    private LocalDate proximaFecha;

    @Column(name = "fk_programado")
    private Integer fkProgramado;

    @Column(name = "odoo_ticket_id", length = 50)
    private String odooTicketId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_origen", length = 30)
    private TipoOrigenMantenimiento tipoOrigen;

    @Column(name = "activo")
    private Boolean activo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipo_id", insertable = false, updatable = false)
    private EquiposJpa fkEquipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_programado", insertable = false, updatable = false)
    private MantenimientoProgramadoJpa programadoRel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", insertable = false, updatable = false)
    private CustodiosJpa fkCliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tecnico_id", insertable = false, updatable = false)
    private UsuariosJpa fkUsuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", insertable = false, updatable = false)
    private EmpresaJpa fkEmpresa;

    public Integer getIdMantenimiento() {
        return idMantenimiento;
    }

    public void setIdMantenimiento(Integer idMantenimiento) {
        this.idMantenimiento = idMantenimiento;
    }

    public Integer getEquipoId() {
        return equipoId;
    }

    public void setEquipoId(Integer equipoId) {
        this.equipoId = equipoId;
    }

    public String getSerieSnapshot() {
        return equipoSnapshot != null ? equipoSnapshot.getSerieSnapshot() : null;
    }

    public void setSerieSnapshot(String serieSnapshot) {
        if (equipoSnapshot == null) {
            equipoSnapshot = new EquipoSnapshotEmbeddable();
        }
        equipoSnapshot.setSerieSnapshot(serieSnapshot);
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Integer getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Integer empresaId) {
        this.empresaId = empresaId;
    }

    public LocalDateTime getFechaProgramada() {
        return fechaProgramada;
    }

    public void setFechaProgramada(LocalDateTime fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }

    public LocalDateTime getFecCierre() {
        return fecCierre;
    }

    public void setFecCierre(LocalDateTime fecCierre) {
        this.fecCierre = fecCierre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipoMantenimiento() {
        return tipoMantenimiento;
    }

    public void setTipoMantenimiento(String tipoMantenimiento) {
        this.tipoMantenimiento = tipoMantenimiento;
    }

    public Integer getYearSnapshoted() {
        return equipoSnapshot != null ? equipoSnapshot.getYearSnapshoted() : null;
    }

    public void setYearSnapshoted(Integer yearSnapshoted) {
        if (equipoSnapshot == null) {
            equipoSnapshot = new EquipoSnapshotEmbeddable();
        }
        equipoSnapshot.setYearSnapshoted(yearSnapshoted);
    }

    public String getCodigoInternoSnapshot() {
        return equipoSnapshot != null ? equipoSnapshot.getCodigoInternoSnapshot() : null;
    }

    /** @deprecated Usar {@link #getCodigoInternoSnapshot()} */
    @Deprecated
    public String getSineSnapshot() {
        return getCodigoInternoSnapshot();
    }

    /** @deprecated Usar {@link #getCodigoInternoSnapshot()} */
    @Deprecated
    public String getSineSnapshoted() {
        return getCodigoInternoSnapshot();
    }

    public void setCodigoInternoSnapshot(String codigoInterno) {
        if (equipoSnapshot == null) {
            equipoSnapshot = new EquipoSnapshotEmbeddable();
        }
        equipoSnapshot.setCodigoInternoSnapshot(codigoInterno);
    }

    /** @deprecated Usar {@link #setCodigoInternoSnapshot(String)} */
    @Deprecated
    public void setSineSnapshot(String sineSnapshoted) {
        setCodigoInternoSnapshot(sineSnapshoted);
    }

    /** @deprecated Usar {@link #setCodigoInternoSnapshot(String)} */
    @Deprecated
    public void setSineSnapshoted(String sineSnapshoted) {
        setCodigoInternoSnapshot(sineSnapshoted);
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }

    public EstadoInternoMantenimiento getEstadoInterno() {
        return estadoInterno;
    }

    public void setEstadoInterno(EstadoInternoMantenimiento estadoInterno) {
        this.estadoInterno = estadoInterno;
    }

    public EquiposJpa getFkEquipo() {
        return fkEquipo;
    }

    public void setFkEquipo(EquiposJpa fkEquipo) {
        this.fkEquipo = fkEquipo;
    }

    public CustodiosJpa getFkCliente() {
        return fkCliente;
    }

    public void setFkCliente(CustodiosJpa fkCliente) {
        this.fkCliente = fkCliente;
    }

    public UsuariosJpa getFkUsuario() {
        return fkUsuario;
    }

    public void setFkUsuario(UsuariosJpa fkUsuario) {
        this.fkUsuario = fkUsuario;
    }

    public String getEstadoGeneral() {
        return estadoGeneral;
    }

    public void setEstadoGeneral(String estadoGeneral) {
        this.estadoGeneral = estadoGeneral;
    }

    public LocalDate getProximaFecha() {
        return proximaFecha;
    }

    public void setProximaFecha(LocalDate proximaFecha) {
        this.proximaFecha = proximaFecha;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Integer getFkProgramado() {
        return fkProgramado;
    }

    public void setFkProgramado(Integer fkProgramado) {
        this.fkProgramado = fkProgramado;
    }

    public String getOdooTicketId() {
        return odooTicketId;
    }

    public void setOdooTicketId(String odooTicketId) {
        this.odooTicketId = odooTicketId;
    }

    public TipoOrigenMantenimiento getTipoOrigen() {
        return tipoOrigen;
    }

    public void setTipoOrigen(TipoOrigenMantenimiento tipoOrigen) {
        this.tipoOrigen = tipoOrigen;
    }

    public EquipoSnapshotEmbeddable getEquipoSnapshot() {
        return equipoSnapshot;
    }

    public void setEquipoSnapshot(EquipoSnapshotEmbeddable equipoSnapshot) {
        this.equipoSnapshot = equipoSnapshot;
    }

    public MantenimientoProgramadoJpa getProgramadoRel() {
        return programadoRel;
    }

    public void setProgramadoRel(MantenimientoProgramadoJpa programadoRel) {
        this.programadoRel = programadoRel;
    }

    public List<MantenimientoEquipoJpa> getEquipos() {
        return equipos;
    }

    public void setEquipos(List<MantenimientoEquipoJpa> equipos) {
        this.equipos = equipos;
    }
}
