package com.uisrael.gestionactivosapi.aplicacion.servicios;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.uisrael.gestionactivosapi.presentacion.dto.request.ImagenMantenimientoRequestDTO;

@Service
public class MantenimientoArchivoService {

    private final Path basePath;
    private static final long MAX_IMAGE_BYTES = 5L * 1024 * 1024;

    public MantenimientoArchivoService(
            @Value("${mantenimiento.storage.base-path:./data/mantenimientos}") String basePath) {
        this.basePath = Path.of(basePath);
    }

    public Path guardarPdf(Integer idMantenimiento, byte[] pdfBytes) {
        try {
            Path carpeta = basePath.resolve("pdfs");
            Files.createDirectories(carpeta);
            Path pdf = carpeta.resolve("mantenimiento_" + idMantenimiento + ".pdf");
            Files.write(pdf, pdfBytes);
            return pdf;
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar el PDF del mantenimiento", e);
        }
    }

    public List<ImagenMantenimientoRequestDTO> guardarImagenes(Integer idMantenimiento, List<MultipartFile> imagenes) {
        if (imagenes == null || imagenes.isEmpty()) {
            return List.of();
        }
        try {
            Path carpeta = basePath.resolve("imagenes").resolve(String.valueOf(idMantenimiento));
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

    public boolean existePdf(Integer idMantenimiento) {
        return Files.exists(obtenerRutaPdf(idMantenimiento));
    }

    private Path obtenerRutaPdf(Integer idMantenimiento) {
        return basePath.resolve("pdfs").resolve("mantenimiento_" + idMantenimiento + ".pdf");
    }
}
