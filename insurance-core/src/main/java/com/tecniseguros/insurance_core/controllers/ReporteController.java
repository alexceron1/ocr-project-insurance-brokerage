package com.tecniseguros.insurance_core.controllers;

import com.tecniseguros.insurance_core.services.ReporteService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {
    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/siniestralidad/filtrado")
    public ResponseEntity<InputStreamResource> descargarReporteFiltrado(
            @RequestParam("flotaId") Integer flotaId,
            @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        try {
            ByteArrayInputStream stream = reporteService.generarReporteSiniestralidadFiltrado(flotaId, fechaInicio, fechaFin);

            String filename = "reporte_flota_" + flotaId + ".xlsx";
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=" + filename);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(new InputStreamResource(stream));
                    
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
