package com.uisrael.gestionactivosapi.presentacion.controladores;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.comandos.CrearAdminSetupCommand;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.ISetupInicialUseCase;

@RestController
@RequestMapping("/api/setup")
public class SetupControlador {

    private final ISetupInicialUseCase setupInicialUseCase;

    public SetupControlador(ISetupInicialUseCase setupInicialUseCase) {
        this.setupInicialUseCase = setupInicialUseCase;
    }

    @GetMapping("/necesario")
    public Map<String, Boolean> esNecesario() {
        return Map.of("necesario", setupInicialUseCase.esNecesario());
    }

    @PostMapping("/admin")
    public ResponseEntity<Map<String, String>> crearAdmin(@RequestBody Map<String, String> datos) {
        if (datos == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El cuerpo de la solicitud es obligatorio."));
        }

        try {
            setupInicialUseCase.crearAdmin(new CrearAdminSetupCommand(
                    datos.get("nombre"),
                    datos.get("correo"),
                    datos.get("contrasena"),
                    datos.get("cedula")));
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("mensaje", "Administrador creado exitosamente."));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", ex.getMessage()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}
