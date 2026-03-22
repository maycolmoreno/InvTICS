package com.uisrael.consumogestionactivosapi.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.ImagenMantenimientoRequestDTO;

@Service
public class MantenimientoArchivoService {

    private static final long MAX_IMAGE_BYTES = 5L * 1024 * 1024;
    private static final Path BASE_UPLOADS = Paths.get("uploads");

    public List<ImagenMantenimientoRequestDTO> guardarImagenes(Integer idMantenimiento, List<MultipartFile> imagenes) {
        if (imagenes == null || imagenes.isEmpty()) {
            return List.of();
        }
        try {
            Path carpeta = BASE_UPLOADS.resolve("mantenimientos").resolve(String.valueOf(idMantenimiento));
            Files.createDirectories(carpeta);
            List<ImagenMantenimientoRequestDTO> metadata = new ArrayList<>();
            for (MultipartFile imagen : imagenes) {
                if (imagen == null || imagen.isEmpty()) {
                    continue;
                }
                if (imagen.getSize() > MAX_IMAGE_BYTES) {
                    throw new IllegalArgumentException("Una imagen supera el limite de 5MB");
                }
                String original = imagen.getOriginalFilename() != null ? imagen.getOriginalFilename() : "evidencia";
                Path destino = carpeta.resolve(original);
                Files.copy(imagen.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
                ImagenMantenimientoRequestDTO dto = new ImagenMantenimientoRequestDTO();
                dto.setNombreArchivo(original);
                dto.setRutaArchivo(destino.toString().replace('\\', '/'));
                dto.setTamanioBytes(imagen.getSize());
                metadata.add(dto);
            }
            return metadata;
        } catch (IOException e) {
            throw new RuntimeException("No se pudieron guardar las imagenes del mantenimiento", e);
        }
    }

    public Path guardarPdf(Integer idMantenimiento, byte[] pdfBytes) {
        try {
            Path carpeta = BASE_UPLOADS.resolve("pdfs");
            Files.createDirectories(carpeta);
            Path pdf = carpeta.resolve("mantenimiento_" + idMantenimiento + ".pdf");
            Files.write(pdf, pdfBytes);
            return pdf;
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar el PDF del mantenimiento", e);
        }
    }

    public Path obtenerRutaPdf(Integer idMantenimiento) {
        return BASE_UPLOADS.resolve("pdfs").resolve("mantenimiento_" + idMantenimiento + ".pdf");
    }

    public byte[] leerPdf(Integer idMantenimiento) {
        try {
            Path pdf = obtenerRutaPdf(idMantenimiento);
            if (!Files.exists(pdf)) {
                throw new IllegalArgumentException("No existe el PDF del mantenimiento solicitado");
            }
            return Files.readAllBytes(pdf);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer el PDF del mantenimiento", e);
        }
    }
}
