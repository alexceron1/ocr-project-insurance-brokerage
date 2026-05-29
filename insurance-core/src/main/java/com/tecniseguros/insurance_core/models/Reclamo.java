package com.tecniseguros.insurance_core.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;

@Entity
@Table(name = "reclamos")
@Data
public class Reclamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reclamo_id")
    private Integer reclamoId;

    @Column(name = "fecha_ocurrencia")
    private LocalDate fechaOcurrencia;

    @Column(name = "hora_ocurrencia")
    private LocalTime horaOcurrencia;

    @Column(name = "nombre_conductor", length = 150)
    private String nombreConductor;

    @Column(name = "descripcion_danos", length = 250)
    private String descripcionDanos;

    @Column(name = "costo_perdida_estimado", precision = 10, scale = 2)
    private BigDecimal costoPerdidaEstimado = BigDecimal.ZERO;

    @Column(name = "pago_efectuado", precision = 10, scale = 2)
    private BigDecimal pagoEfectuado = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private ReclamoEstatus estatus = ReclamoEstatus.pendiente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "automovil_id")
    private Automovil automovil;

    @Column(name = "tipo_siniestro")
    private Integer tipoSiniestro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taller_id")
    private Taller taller;
}
