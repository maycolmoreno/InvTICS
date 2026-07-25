package com.uisrael.gestionactivosapi.infraestructura.servicios;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import com.uisrael.gestionactivosapi.presentacion.dto.request.ImagenMantenimientoRequestDTO;

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
                String nombreSeguro = sanearNombreArchivo(imagen.getOriginalFilename());
                Path destino = carpeta.resolve(nombreSeguro).normalize();
                if (!destino.startsWith(carpeta)) {
                    throw new IllegalArgumentException("Nombre de archivo no valido: " + imagen.getOriginalFilename());
                }
                Files.copy(imagen.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

                ImagenMantenimientoRequestDTO dto = new ImagenMantenimientoRequestDTO();
                dto.setNombreArchivo(nombreSeguro);
                dto.setRutaArchivo(destino.toString().replace('\\', '/'));
                dto.setTamanioBytes(imagen.getSize());
                metadata.add(dto);
            }
            return metadata;
        } catch (IOException e) {
            throw new RuntimeException("No se pudieron guardar las imagenes del mantenimiento", e);
        }
    }

    /**
     * El nombre lo envia el cliente y puede traer rutas ("../", "C:\...") o
     * caracteres fuera de [a-zA-Z0-9._-], el unico patron que despues acepta
     * el endpoint que sirve las imagenes: se recorta al nombre base y todo
     * caracter fuera de ese patron se reemplaza por "_" para que lo guardado
     * sea siempre servible.
     */
    static String sanearNombreArchivo(String original) {
        String nombre = original == null ? "" : original;
        int separador = Math.max(nombre.lastIndexOf('/'), nombre.lastIndexOf('\\'));
        if (separador >= 0) {
            nombre = nombre.substring(separador + 1);
        }
        nombre = nombre.replaceAll("[^a-zA-Z0-9._-]", "_");
        boolean sinContenido = nombre.chars().allMatch(c -> c == '.' || c == '_');
        return sinContenido ? "evidencia" : nombre;
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

    /**
     * Se invoca cuando falla la regeneracion del PDF al cerrar una OT: sin esto,
     * el PDF cacheado de antes del cierre (ej. "en proceso", sin resultado
     * tecnico) queda serviendose para siempre como si fuera el informe final.
     * Al borrarlo, la proxima consulta fuerza una regeneracion real en vez de
     * mostrar contenido obsoleto en silencio.
     */
    public void eliminarPdfSiExiste(Integer idMantenimiento) {
        try {
            Files.deleteIfExists(obtenerRutaPdf(idMantenimiento));
        } catch (IOException e) {
            throw new RuntimeException("No se pudo eliminar el PDF cacheado del mantenimiento", e);
        }
    }

    public byte[] leerImagen(Integer idMantenimiento, String nombreArchivo) {
        try {
            Path imagen = basePath.resolve("imagenes")
                    .resolve(String.valueOf(idMantenimiento))
                    .resolve(nombreArchivo)
                    .normalize();
            if (!imagen.startsWith(basePath.resolve("imagenes").normalize())) {
                throw new IllegalArgumentException("Ruta de imagen no valida");
            }
            if (!Files.exists(imagen)) {
                return null;
            }
            return Files.readAllBytes(imagen);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer la imagen", e);
        }
    }

    public String detectarTipoMime(String nombreArchivo) {
        String lower = nombreArchivo.toLowerCase();
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".gif")) return "image/gif";
        if (lower.endsWith(".webp")) return "image/webp";
        return "image/jpeg";
    }

    private Path obtenerRutaPdf(Integer idMantenimiento) {
        return basePath.resolve("pdfs").resolve("mantenimiento_" + idMantenimiento + ".pdf");
    }
}
