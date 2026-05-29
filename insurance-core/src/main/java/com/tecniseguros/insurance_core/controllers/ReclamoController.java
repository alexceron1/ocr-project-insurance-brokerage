package com.tecniseguros.insurance_core.controllers;

import com.tecniseguros.insurance_core.models.Reclamo;
import com.tecniseguros.insurance_core.services.ReclamoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reclamos")
@CrossOrigin(origins = "*") // Permite peticiones desde cualquier frontend
public class ReclamoController {
    private final ReclamoService reclamoService;

    // Inyección de dependencias
    public ReclamoController(ReclamoService reclamoService) {
        this.reclamoService = reclamoService;
    }

    @PostMapping("/procesar-pdf")
    public ResponseEntity<?> procesarSiniestroPdf(@RequestParam("file") MultipartFile file) {
        try {
            // 1. Validación básica
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Error: El archivo PDF está vacío o no se adjuntó correctamente.");
            }

            // 2. Ejecutar la lógica de negocio (OCR + Base de Datos)
            Reclamo reclamoGuardado = reclamoService.procesarNuevoReclamo(file);

            // 3. Construir una respuesta JSON de éxito
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Reclamo procesado y guardado exitosamente en PostgreSQL");
            response.put("reclamo_id", reclamoGuardado.getReclamoId());
            response.put("estatus", reclamoGuardado.getEstatus());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            // Manejo de errores de negocio (Ej: "El vehículo con placa X no está registrado")
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // Manejo de errores internos (Ej: Caída del servicio de Python o Base de datos)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor: " + e.getMessage());
        }
    }
}
