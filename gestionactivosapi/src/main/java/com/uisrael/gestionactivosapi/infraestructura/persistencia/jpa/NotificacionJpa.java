package com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "notificaciones")
public class NotificacionJpa implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    private Long idNotificacion;

    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @Column(name = "mensaje", nullable = false, length = 255)
    private String mensaje;

    @Column(name = "url", length = 500)
    private String url;

    @Column(name = "leida")
    private Boolean leida;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    @Column(name = "referencia_mantenimiento_id")
    private Integer referenciaMantenimientoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", insertable = false, updatable = false)
    private UsuariosJpa fkUsuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "referencia_mantenimiento_id", insertable = false, updatable = false)
    private MantenimientosJpa fkMantenimiento;
}
