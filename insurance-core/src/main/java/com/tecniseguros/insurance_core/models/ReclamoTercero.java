package com.tecniseguros.insurance_core.models;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "reclamo_terceros")
@Data
public class ReclamoTercero {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reclamo_tercero_id")
    private Integer reclamoTerceroId;

    @Column(nullable = false, length = 80)
    private String propietario;

    @Column(length = 150)
    private String correo;

    @Column(length = 20)
    private String telefono;

    @Column(nullable = false, length = 250)
    private String descripcion;

    @Column(name = "costo_perdida_estimado", precision = 10, scale = 2)
    private BigDecimal costoPerdidaEstimado = BigDecimal.ZERO;

    @Column(name = "pago_efectuado", precision = 10, scale = 2)
    private BigDecimal pagoEfectuado = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private ReclamoEstatus estatus = ReclamoEstatus.pendiente;

    // Relación con la tabla principal de reclamos
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reclamo_id")
    private Reclamo reclamo;
}
