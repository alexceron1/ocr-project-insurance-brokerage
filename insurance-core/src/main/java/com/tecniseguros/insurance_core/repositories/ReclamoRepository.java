package com.tecniseguros.insurance_core.repositories;

import com.tecniseguros.insurance_core.models.Reclamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface ReclamoRepository extends JpaRepository<Reclamo, Integer> {
    
    @Query("SELECT r FROM Reclamo r " +
           "JOIN FETCH r.automovil a " +
           "LEFT JOIN FETCH a.flota f " +
           "LEFT JOIN FETCH f.asegurado asg " +
           "LEFT JOIN FETCH r.taller t " +
           "WHERE f.flotaId = :flotaId " +
           "AND r.fechaOcurrencia BETWEEN :fechaInicio AND :fechaFin " +
           "ORDER BY r.fechaOcurrencia DESC")
    List<Reclamo> findByFlotaYRangoFechas(
            @Param("flotaId") Integer flotaId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );
}
