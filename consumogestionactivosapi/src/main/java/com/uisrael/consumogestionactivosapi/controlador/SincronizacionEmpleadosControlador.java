package com.uisrael.consumogestionactivosapi.controlador;

import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.sync.EstadoSincronizacionDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.sync.SincronizacionResultadoDTO;
import com.uisrael.consumogestionactivosapi.service.ISyncEmpleadosServicio;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/custodios/sincronizacion")
@RequiredArgsConstructor
public class SincronizacionEmpleadosControlador {

    private final ISyncEmpleadosServicio syncServicio;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping
    public String verConsola(Model model) {
        EstadoSincronizacionDTO estado;
        try {
            estado = syncServicio.obtenerEstado();
        } catch (Exception ex) {
            log.warn("No se pudo obtener el estado de sincronizacion: {}", ex.getMessage());
            estado = new EstadoSincronizacionDTO();
            model.addAttribute("error", "No se pudo consultar el estado de sincronización: " + mensajeError(ex));
        }
        model.addAttribute("estado", estado);
        return "Custodios/sincronizacion";
    }

    @PostMapping("/desde-fuente")
    public String sincronizarDesdeFuente(RedirectAttributes redirect) {
        try {
            SincronizacionResultadoDTO resultado = syncServicio.sincronizarDesdeFuente();
            redirect.addFlashAttribute("success", resumen(resultado));
        } catch (Exception ex) {
            redirect.addFlashAttribute("error", "No se pudo sincronizar desde la fuente: " + mensajeError(ex));
        }
        return "redirect:/custodios/sincronizacion";
    }

    @PostMapping("/archivo")
    public String sincronizarArchivo(@RequestParam("archivo") MultipartFile archivo, RedirectAttributes redirect) {
        if (archivo == null || archivo.isEmpty()) {
            redirect.addFlashAttribute("error", "Seleccione un archivo JSON de empleados.");
            return "redirect:/custodios/sincronizacion";
        }
        try {
            String json = new String(archivo.getBytes(), StandardCharsets.UTF_8);
            SincronizacionResultadoDTO resultado = syncServicio.sincronizarManual(json);
            redirect.addFlashAttribute("success", resumen(resultado));
        } catch (Exception ex) {
            redirect.addFlashAttribute("error", "No se pudo procesar el archivo: " + mensajeError(ex));
        }
        return "redirect:/custodios/sincronizacion";
    }

    private static String resumen(SincronizacionResultadoDTO r) {
        return "Sincronización completada: " + r.getTotalRecibidos() + " recibidos, "
                + r.getCreados() + " creados, " + r.getActualizados() + " actualizados, "
                + r.getInactivados() + " inactivados, " + r.getReactivados() + " reactivados, "
                + r.getAdvertencias() + " advertencia(s).";
    }

    /** Extrae el mensaje {"error": ...} que devuelve el backend, si existe. */
    private String mensajeError(Exception ex) {
        if (ex instanceof RestClientResponseException rex) {
            try {
                JsonNode cuerpo = objectMapper.readTree(rex.getResponseBodyAsString());
                if (cuerpo.hasNonNull("error")) {
                    return cuerpo.get("error").asText();
                }
            } catch (Exception ignorada) {
                // cuerpo no era JSON: usar mensaje generico
            }
            return "el backend respondió " + rex.getStatusCode().value();
        }
        return ex.getMessage() != null ? ex.getMessage() : "error inesperado";
    }
}
