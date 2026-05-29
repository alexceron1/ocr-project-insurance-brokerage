package com.tecniseguros.insurance_core.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "asegurados")
@Data
public class Asegurado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "asegurado_id")
    private Integer aseguradoId;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 150)
    private String correo;

    @Column(length = 20)
    private String telefono;
}
