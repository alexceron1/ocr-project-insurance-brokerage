package com.tecniseguros.insurance_core.services;

import com.tecniseguros.insurance_core.models.*;
import com.tecniseguros.insurance_core.repositories.*;
import com.tecniseguros.insurance_core.dto.OcrReclamoResponse;
import com.tecniseguros.insurance_core.models.Automovil;
import com.tecniseguros.insurance_core.models.Reclamo;
import com.tecniseguros.insurance_core.models.ReclamoEstatus;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class ReclamoService {

    private final AutomovilRepository automovilRepository;
    private final ReclamoRepository reclamoRepository;
    private final ReclamoTerceroRepository reclamoTerceroRepository;
    private final RestTemplate restTemplate;

    public ReclamoService(AutomovilRepository automovilRepository, 
                          ReclamoRepository reclamoRepository, 
                          ReclamoTerceroRepository reclamoTerceroRepository,
                          RestTemplate restTemplate) {
        this.automovilRepository = automovilRepository;
        this.reclamoRepository = reclamoRepository;
        this.reclamoTerceroRepository = reclamoTerceroRepository;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public Reclamo procesarNuevoReclamo(MultipartFile archivoPdf) {
        // 1. Llamada al microservicio de Python
        String pythonApiUrl = "https://ocr-project-insurance-brokerage.onrender.com"; //"http://ocr-service:8000/procesar"; // "http://localhost:8000/api/extract";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", archivoPdf.getResource());
        
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<OcrReclamoResponse> response = restTemplate.postForEntity(
                pythonApiUrl, requestEntity, OcrReclamoResponse.class);
                
        OcrReclamoResponse ocrData = response.getBody();
        if (ocrData == null || !"success".equals(ocrData.getStatus())) {
            throw new RuntimeException("Error al procesar el documento en el motor OCR");
        }

        OcrReclamoResponse.DatosExtraidos datos = ocrData.getDatos_extraidos();
        
        // 2. Buscar el vehículo directamente por placa
        // Si pertenece a una flota, las relaciones mapeadas permitirán su reportería posterior
        Automovil automovil = automovilRepository.findByPlaca(datos.getClave_busqueda().getPlaca())
                .orElseThrow(() -> new RuntimeException("El vehículo con placa " + 
                        datos.getClave_busqueda().getPlaca() + " no está registrado en el sistema"));

        // 3. Crear el Reclamo
        Reclamo nuevoReclamo = new Reclamo();
        nuevoReclamo.setAutomovil(automovil);
        nuevoReclamo.setNombreConductor(datos.getReclamo().getNombre_conductor());
        nuevoReclamo.setDescripcionDanos(datos.getReclamo().getDescripcion_danos());
        nuevoReclamo.setEstatus(ReclamoEstatus.pendiente);
        
        // Parseo de fecha local (ej: "19 de mayo de 2026")
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", Locale.of("es", "ES"));
        nuevoReclamo.setFechaOcurrencia(LocalDate.parse(datos.getReclamo().getFecha_ocurrencia(), dateFormatter));
        nuevoReclamo.setHoraOcurrencia(LocalTime.parse(datos.getReclamo().getHora_ocurrencia()));

        Reclamo reclamoGuardado = reclamoRepository.save(nuevoReclamo);

        // 4. Registrar Tercero si aplica
        if (datos.getTercero().isInvolucrado()) {
            ReclamoTercero tercero = new ReclamoTercero();
            tercero.setReclamo(reclamoGuardado);
            tercero.setPropietario(datos.getTercero().getPropietario());
            tercero.setDescripcion(datos.getTercero().getDescripcion());
            tercero.setEstatus(ReclamoEstatus.pendiente);
            
            reclamoTerceroRepository.save(tercero);
        }

        return reclamoGuardado;
    }
}
