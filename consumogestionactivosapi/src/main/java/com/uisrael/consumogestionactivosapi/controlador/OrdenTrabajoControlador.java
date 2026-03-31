package com.uisrael.consumogestionactivosapi.controlador;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestClientResponseException;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.OrdenCrearRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.OrdenGuardarRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.OrdenCrearResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.OrdenTrabajoResponseDTO;
import com.uisrael.consumogestionactivosapi.service.IOrdenTrabajoServicio;

@Controller
public class OrdenTrabajoControlador {

    private final IOrdenTrabajoServicio ordenServicio;

    public OrdenTrabajoControlador(IOrdenTrabajoServicio ordenServicio) {
        this.ordenServicio = ordenServicio;
    }

    @PostMapping("/orden/crear")
    public ResponseEntity<?> crear(@RequestBody OrdenCrearRequestDTO request) {
        if (request.getTipo() == null || request.getTipo().isBlank()) {
            request.setTipo("PREVENTIVO");
        }
        if (request.getPrioridad() == null || request.getPrioridad().isBlank()) {
            request.setPrioridad("NORMAL");
        }
        try {
            OrdenCrearResponseDTO resp = ordenServicio.crearOrden(request);
            String location = "/orden/" + resp.getIdMantenimiento();
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, location)
                    .build();
        } catch (RestClientResponseException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .body(extraerMensaje(ex.getResponseBodyAsString()));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ex.getMessage() != null ? ex.getMessage() : "No se pudo iniciar mantenimiento.");
        }
    }

    @GetMapping("/orden/{id}")
    public String verOrden(@PathVariable Integer id, Model model) {
        OrdenTrabajoResponseDTO orden = ordenServicio.obtenerOrden(id);
        model.addAttribute("orden", orden);
        return "Orden/orden-trabajo";
    }

    @PostMapping("/orden/{id}/guardar")
    public ResponseEntity<Void> guardar(@PathVariable Integer id, @RequestBody OrdenGuardarRequestDTO request) {
        ordenServicio.guardarOrden(id, request);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, "/visita")
                .build();
    }

    private String extraerMensaje(String body) {
        if (body == null || body.isBlank()) {
            return "No se pudo iniciar mantenimiento.";
        }
        String token = "\"message\":\"";
        int ini = body.indexOf(token);
        if (ini < 0) {
            return body;
        }
        int start = ini + token.length();
        int end = body.indexOf("\"", start);
        if (end <= start) {
            return body;
        }
        return body.substring(start, end)
                .replace("\\n", " ")
                .replace("\\\"", "\"");
    }
}
