package com.tecniseguros.insurance_core.repositories;

import com.tecniseguros.insurance_core.models.Automovil;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AutomovilRepository extends JpaRepository<Automovil, Integer> {
    Optional<Automovil> findByPlaca(String placa);
}
