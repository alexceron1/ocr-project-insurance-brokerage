package com.tecniseguros.insurance_core.services;

import com.tecniseguros.insurance_core.models.Reclamo;
import com.tecniseguros.insurance_core.repositories.ReclamoRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReporteService {
    private final ReclamoRepository reclamoRepository;
    // Si necesitas buscar el nombre del asegurado directo de su repositorio porque no está mapeado el objeto completo:
    // private final AseguradoRepository aseguradoRepository; 

    public ReporteService(ReclamoRepository reclamoRepository) {
        this.reclamoRepository = reclamoRepository;
    }

    public ByteArrayInputStream generarReporteSiniestralidadFiltrado(Integer flotaId, LocalDate inicio, LocalDate fin) throws IOException {
        
        // Nueva estructura de columnas solicitada
        String[] columnas = {
            "ID Reclamo", "Asegurado", "Flota ID", "Fecha Ocurrencia", 
            "Placa", "Marca", "Modelo", "Valor Asegurado", 
            "Conductor", "Costo Estimado", "Pago Efectuado", "Taller", "Estatus"
        };
        
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Reporte Filtrado");

            // Estilos del encabezado
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Dibujar encabezados
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columnas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(headerCellStyle);
            }

            // Consultar datos filtrados desde PostgreSQL
            List<Reclamo> reclamos = reclamoRepository.findByFlotaYRangoFechas(flotaId, inicio, fin);
            int rowIdx = 1;
            
            for (Reclamo reclamo : reclamos) {
                Row row = sheet.createRow(rowIdx++);

                // Columna 0: ID Reclamo
                row.createCell(0).setCellValue(reclamo.getReclamoId());
                
                // Navegación relacional hacia Automóvil, Flota y Asegurado
                if (reclamo.getAutomovil() != null) {
                    var flota = reclamo.getAutomovil().getFlota();
                    
                    // Columna 1: Nombre Real del Asegurado
                    if (flota != null && flota.getAsegurado() != null) {
                        row.createCell(1).setCellValue(flota.getAsegurado().getNombre());
                    } else {
                        row.createCell(1).setCellValue("Sin Asegurado");
                    }
                    
                    // Columna 2: Flota ID
                    if (flota != null) {
                        row.createCell(2).setCellValue(flota.getFlotaId());
                    } else {
                        row.createCell(2).setCellValue("N/A");
                    }
                    
                    // Columnas de Automóvil
                    row.createCell(4).setCellValue(reclamo.getAutomovil().getPlaca());
                    row.createCell(5).setCellValue(reclamo.getAutomovil().getMarca());
                    row.createCell(6).setCellValue(reclamo.getAutomovil().getModelo());
                    
                    double valorAsegurado = reclamo.getAutomovil().getValorAsegurado() != null ? 
                            reclamo.getAutomovil().getValorAsegurado().doubleValue() : 0.0;
                    row.createCell(7).setCellValue(valorAsegurado);
                } else {
                    row.createCell(1).setCellValue("N/A");
                    row.createCell(2).setCellValue("N/A");
                    row.createCell(4).setCellValue("N/A");
                    row.createCell(5).setCellValue("N/A");
                    row.createCell(6).setCellValue("N/A");
                    row.createCell(7).setCellValue(0.0);
                }

                // Datos Directos del Reclamo
                row.createCell(3).setCellValue(reclamo.getFechaOcurrencia() != null ? reclamo.getFechaOcurrencia().toString() : "N/A");
                row.createCell(8).setCellValue(reclamo.getNombreConductor() != null ? reclamo.getNombreConductor() : "N/A");
                
                double costoEstimado = reclamo.getCostoPerdidaEstimado() != null ? reclamo.getCostoPerdidaEstimado().doubleValue() : 0.0;
                row.createCell(9).setCellValue(costoEstimado);
                
                double pagoEfectuado = reclamo.getPagoEfectuado() != null ? reclamo.getPagoEfectuado().doubleValue() : 0.0;
                row.createCell(10).setCellValue(pagoEfectuado);
                
                // Columna 11: Nombre Real del Taller
                if (reclamo.getTaller() != null) {
                    row.createCell(11).setCellValue(reclamo.getTaller().getNombre());
                } else {
                    row.createCell(11).setCellValue("No asignado");
                }
                
                row.createCell(12).setCellValue(reclamo.getEstatus().name().toUpperCase());
            }
            // Auto-ajustar columnas
            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}
