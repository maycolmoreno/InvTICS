package com.uisrael.consumogestionactivosapi.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ActaStorageService {

    private static final Logger log = LoggerFactory.getLogger(ActaStorageService.class);

    private final Path basePath;
    private final RestClient clienteWeb;

    public ActaStorageService(
            @Value("${actas.storage.base-path:./uploads/actas}") String basePath,
            RestClient clienteWeb) {
        this.basePath = Paths.get(basePath);
        this.clienteWeb = clienteWeb;
    }

    public String guardarActaPdf(byte[] pdfBytes, String tipoMovimiento, int idCustodio, LocalDate fecha) throws IOException {
        Files.createDirectories(basePath);

        String fechaStr = fecha != null ? fecha.format(DateTimeFormatter.ofPattern("yyyyMMdd")) : "sin-fecha";
        String nombreArchivo = String.format("acta_%s_%d_%s.pdf",
                tipoMovimiento.toLowerCase(), idCustodio, fechaStr);

        Path archivoDestino = basePath.resolve(nombreArchivo);
        Files.write(archivoDestino, pdfBytes);

        log.info("Acta PDF guardada: {}", archivoDestino);
        return nombreArchivo;
    }

    public void registrarRutaEnCustodias(List<Integer> idsCustodias, String nombreArchivo) {
        try {
            String idsParam = idsCustodias.stream()
                    .map(String::valueOf)
                    .reduce((a, b) -> a + "," + b)
                    .orElse("");

            clienteWeb.put()
                    .uri(uriBuilder -> uriBuilder
                            .path("/custodias/acta-pdf")
                            .queryParam("ids", idsParam)
                            .queryParam("rutaPdf", nombreArchivo)
                            .build())
                    .retrieve()
                    .toBodilessEntity();

            log.info("Ruta PDF registrada en {} custodias: {}", idsCustodias.size(), nombreArchivo);
        } catch (Exception e) {
            log.error("Error al registrar ruta del PDF en custodias: {}", e.getMessage());
        }
    }

    public byte[] leerActaPdf(String nombreArchivo) throws IOException {
        Path archivo = basePath.resolve(nombreArchivo);
        if (!Files.exists(archivo)) {
            return null;
        }
        return Files.readAllBytes(archivo);
    }

    public boolean existeActa(String nombreArchivo) {
        if (nombreArchivo == null || nombreArchivo.isBlank()) {
            return false;
        }
        return Files.exists(basePath.resolve(nombreArchivo));
    }
}
