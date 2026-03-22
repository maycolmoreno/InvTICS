package com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa;

import java.io.Serializable;
import java.math.BigDecimal;

import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.base.AuditableEntity;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ubicaciones")
@SQLRestriction("deleted_at IS NULL")
public class UbicacionesJpa extends AuditableEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_ubicacion")
	private int idUbicacion;

	@Column(name = "nombre", length = 100, nullable = false)
	private String nombre;

	@Column(name = "agencia", length = 100, nullable = false)
	private String agencia;

	private boolean estado;

	@Column(name = "latitud", precision = 10, scale = 7)
	private BigDecimal latitud;

	@Column(name = "longitud", precision = 10, scale = 7)
	private BigDecimal longitud;

	@Column(name = "direccion", length = 255)
	private String direccion;

	@Column(name = "ciudad", length = 100)
	private String ciudad;

	@Column(name = "parroquia", length = 150)
	private String parroquia;

	@Column(name = "provincia", length = 100)
	private String provincia;

	@Column(name = "link_coordenada", length = 500)
	private String linkCoordenada;

}
