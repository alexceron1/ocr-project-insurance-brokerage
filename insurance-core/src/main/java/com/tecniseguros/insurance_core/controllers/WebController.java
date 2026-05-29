package com.tecniseguros.insurance_core.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
    @GetMapping("/")
    public String mostrarPanelPrincipal(Model model) {
        // Aquí le decimos a Spring que busque el archivo "index.html" en la carpeta templates
        model.addAttribute("titulo", "TecniSeguros - Gestión de Reclamos");
        return "index"; 
    }
}
