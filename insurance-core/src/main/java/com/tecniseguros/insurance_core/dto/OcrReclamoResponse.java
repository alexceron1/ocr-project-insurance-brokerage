package com.tecniseguros.insurance_core.dto;

import lombok.Data;

@Data
public class OcrReclamoResponse {
    private String status;
    private String filename;
    private DatosExtraidos datos_extraidos;

    @Data
    public static class DatosExtraidos {
        private ClaveBusqueda clave_busqueda;
        private ReclamoData reclamo;
        private TerceroData tercero;
    }

    @Data
    public static class ClaveBusqueda {
        private String placa;
        private String taller_nombre;
    }

    @Data
    public static class ReclamoData {
        private String fecha_ocurrencia;
        private String hora_ocurrencia;
        private String nombre_conductor;
        private String descripcion_danos;
    }

    @Data
    public static class TerceroData {
        private boolean involucrado;
        private String propietario;
        private String descripcion;
    }
}
