package com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "custodias")
public class CustodiasJpa implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_custodia_equipo")
    private int idCustodiaEquipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_equipo", nullable = false)
    private EquiposJpa fkEquipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_custodio", nullable = false)
    private CustodiosJpa fkCustodio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_ubicacion")
    private UbicacionesJpa fkUbicacion;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    private String observacion;

    private boolean estado;

    @Column(name = "tipo_movimiento")
    private String tipoMovimiento;
}
