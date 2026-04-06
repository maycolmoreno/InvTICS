package com.uisrael.gestionactivosapi.dominio.entidades;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Mantenimientos {

    private Integer idMantenimiento;
    private Integer equipoId;
    private EquipoSnapshot equipoSnapshot;
    private Integer idCliente;
    private Integer empresaId;
    private LocalDateTime fechaProgramada;
    private LocalDateTime fecCierre;
    private Integer frecuenciaDias;
    private String descripcion;
    private String tipoMantenimiento;
    private String estado;
    private LocalDateTime creadoEn;
    private EstadoInternoMantenimiento estadoInterno;
    private Integer idUsuario;
    private String estadoGeneral;
    private LocalDate proximaFecha;
    private Integer fkProgramado;
    private String odooTicketId;
    private TipoOrigenMantenimiento tipoOrigen;
    private Boolean activo;

    public Mantenimientos() {
    }

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
        return equipoSnapshot != null ? equipoSnapshot.serieSnapshot() : null;
    }

    public void setSerieSnapshot(String serieSnapshot) {
        this.equipoSnapshot = new EquipoSnapshot(serieSnapshot, getSineSnapshot(), getYearSnapshoted());
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

    public Integer getFrecuenciaDias() {
        return frecuenciaDias;
    }

    public void setFrecuenciaDias(Integer frecuenciaDias) {
        this.frecuenciaDias = frecuenciaDias;
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

    public Integer getYearSnapshoted() {
        return equipoSnapshot != null ? equipoSnapshot.yearSnapshoted() : null;
    }

    public void setYearSnapshoted(Integer yearSnapshoted) {
        this.equipoSnapshot = new EquipoSnapshot(getSerieSnapshot(), getSineSnapshot(), yearSnapshoted);
    }

    public String getSineSnapshot() {
        return equipoSnapshot != null ? equipoSnapshot.sineSnapshot() : null;
    }

    public String getSineSnapshoted() {
        return getSineSnapshot();
    }

    public void setSineSnapshot(String sineSnapshot) {
        this.equipoSnapshot = new EquipoSnapshot(getSerieSnapshot(), sineSnapshot, getYearSnapshoted());
    }

    public void setSineSnapshoted(String sineSnapshoted) {
        setSineSnapshot(sineSnapshoted);
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public EquipoSnapshot getEquipoSnapshot() {
        return equipoSnapshot;
    }

    public void setEquipoSnapshot(EquipoSnapshot equipoSnapshot) {
        this.equipoSnapshot = equipoSnapshot;
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
}
