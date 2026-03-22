package com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.uisrael.gestionactivosapi.dominio.entidades.TipoFirma;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "firmas_mantenimiento")
public class FirmaMantenimientoJpa implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_mantenimiento", nullable = false)
    private Integer idMantenimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_firma", nullable = false, length = 20)
    private TipoFirma tipoFirma;

    @Column(name = "firma_base64", columnDefinition = "TEXT", nullable = false)
    private String firmaBase64;

    @Column(name = "firmado_en", nullable = false)
    private LocalDateTime firmadoEn;

    @Column(name = "ip_origen", length = 45)
    private String ipOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mantenimiento", insertable = false, updatable = false)
    private MantenimientosJpa mantenimiento;
}
