package com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "actualizacion_activos")
public class ActualizacionActivoJpa {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "activo_id", nullable = false)
	private Integer activoId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_actualizacion")
	private Date fechaActualizacion;

	@Column(columnDefinition = "TEXT")
	private String descripcion;

	@Column(name = "usuario_actualizacion", length = 100)
	private String usuarioActualizacion;

	@Column(name = "fk_usuario_actualizacion")
	private Integer fkUsuarioActualizacion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_usuario_actualizacion", insertable = false, updatable = false)
	private UsuariosJpa usuarioRel;

	public ActualizacionActivoJpa() {
	}

	// Getters and Setters
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getActivoId() {
		return activoId;
	}

	public void setActivoId(Integer activoId) {
		this.activoId = activoId;
	}

	public Date getFechaActualizacion() {
		return fechaActualizacion;
	}

	public void setFechaActualizacion(Date fechaActualizacion) {
		this.fechaActualizacion = fechaActualizacion;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getUsuarioActualizacion() {
		return usuarioActualizacion;
	}

	public void setUsuarioActualizacion(String usuarioActualizacion) {
		this.usuarioActualizacion = usuarioActualizacion;
	}

	public Integer getFkUsuarioActualizacion() {
		return fkUsuarioActualizacion;
	}

	public void setFkUsuarioActualizacion(Integer fkUsuarioActualizacion) {
		this.fkUsuarioActualizacion = fkUsuarioActualizacion;
	}

	public UsuariosJpa getUsuarioRel() {
		return usuarioRel;
	}

	public void setUsuarioRel(UsuariosJpa usuarioRel) {
		this.usuarioRel = usuarioRel;
	}
}
