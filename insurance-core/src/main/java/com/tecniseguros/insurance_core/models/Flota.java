package com.tecniseguros.insurance_core.models;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "flotas")
@Data
public class Flota {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "flota_id")
    private Integer flotaId;

    @Column(unique = true, nullable = false, length = 50)
    private String clave;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(length = 150)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asegurado_id")
    private Asegurado asegurado;

    @OneToMany(mappedBy = "flota")
    private List<Automovil> automoviles;
}
