package com.tecniseguros.insurance_core.models;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "automoviles")
@Data
public class Automovil {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "automovil_id")
    private Integer automovilId;

    @Column(nullable = false, length = 100)
    private String marca;

    @Column(nullable = false, length = 100)
    private String modelo;

    @Column(nullable = false)
    private Short fabricacion; // smallint en PostgreSQL

    @Column(length = 20)
    private String color;

    @Column(name = "valor_asegurado", precision = 10, scale = 2)
    private BigDecimal valorAsegurado;

    @Column(name = "prima_pagar", precision = 10, scale = 2)
    private BigDecimal primaPagar;

    @Column(name = "poliza_id")
    private Integer polizaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flota_id")
    private Flota flota;

    @Column(name = "placa", length = 25)
    private String placa;
}
