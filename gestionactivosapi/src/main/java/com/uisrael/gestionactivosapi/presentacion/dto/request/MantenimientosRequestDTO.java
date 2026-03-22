package com.uisrael.gestionactivosapi.presentacion.dto.request;

import java.time.LocalDateTime;

public class MantenimientosRequestDTO {

    private Integer id;
    private Integer equipoId;
    private String serieSnapshot;
    private Integer idCliente;
    private Integer empresaId;
    private LocalDateTime fechaProgramada;
    private LocalDateTime fecCierre;
    private Integer frecuenciaDias;
    private String descripcion;
    private String tipoMantenimiento;
    private String estado;
    private LocalDateTime creadoEn;
    private String estadoInterno;
    private Integer yearSnapshoted;
    private String sineSnapshoted;
    private Integer idUsuario;
    private Integer fkProgramado;
    private String odooTicketId;
    private String tipoOrigen;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEquipoId() {
        return equipoId;
    }

    public void setEquipoId(Integer equipoId) {
        this.equipoId = equipoId;
    }

    public String getSerieSnapshot() {
        return serieSnapshot;
    }

    public void setSerieSnapshot(String serieSnapshot) {
        this.serieSnapshot = serieSnapshot;
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

    public String getEstadoInterno() {
        return estadoInterno;
    }

    public void setEstadoInterno(String estadoInterno) {
        this.estadoInterno = estadoInterno;
    }

    public Integer getYearSnapshoted() {
        return yearSnapshoted;
    }

    public void setYearSnapshoted(Integer yearSnapshoted) {
        this.yearSnapshoted = yearSnapshoted;
    }

    public String getSineSnapshoted() {
        return sineSnapshoted;
    }

    public void setSineSnapshoted(String sineSnapshoted) {
        this.sineSnapshoted = sineSnapshoted;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
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

    public String getTipoOrigen() {
        return tipoOrigen;
    }

    public void setTipoOrigen(String tipoOrigen) {
        this.tipoOrigen = tipoOrigen;
    }
}
