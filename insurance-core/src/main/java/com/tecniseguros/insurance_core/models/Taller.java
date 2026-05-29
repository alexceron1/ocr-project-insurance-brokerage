package com.tecniseguros.insurance_core.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "talleres")
@Data
public class Taller {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "taller_id")
    private Integer tallerId;

    @Column(nullable = false, unique = true, length = 150)
    private String nombre;

    @Column(nullable = false, length = 150)
    private String correo;

    @Column(nullable = false, length = 20)
    private String telefono;
}
