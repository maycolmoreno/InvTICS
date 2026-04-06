package com.uisrael.consumogestionactivosapi.controlador;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.FilaImportDTO;
import com.uisrael.consumogestionactivosapi.service.ICategoriaEquiposServicio;
import com.uisrael.consumogestionactivosapi.service.IImportarServicio;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/importar")
public class ImportarControlador {

    private static final String SESSION_KEY = "filas_import";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String XLSX_MIME = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private final IImportarServicio servicioImportar;
    private final ICategoriaEquiposServicio servicioCategorias;

    @GetMapping
    public String mostrarFormulario(Model model) {
        model.addAttribute("categorias", servicioCategorias.listarCategoriaEquipo());
        return "importar/importarEquipos";
    }

    @PostMapping("/preview")
    public String preview(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("categoriaId") int categoriaId,
            HttpSession session,
            Model model,
            RedirectAttributes ra) {

        if (archivo.isEmpty()) {
            ra.addFlashAttribute("error", "Debe seleccionar un archivo .xlsx");
            return "redirect:/importar";
        }

        if (archivo.getSize() > MAX_FILE_SIZE) {
            ra.addFlashAttribute("error", "El archivo excede el tamaño máximo permitido (5MB).");
            return "redirect:/importar";
        }

        String contentType = archivo.getContentType();
        String originalFilename = archivo.getOriginalFilename();
        if ((contentType == null || !contentType.equals(XLSX_MIME))
                && (originalFilename == null || !originalFilename.toLowerCase().endsWith(".xlsx"))) {
            ra.addFlashAttribute("error", "Solo se permiten archivos .xlsx (Excel).");
            return "redirect:/importar";
        }

        try {
            List<FilaImportDTO> filas = servicioImportar.parsearExcel(archivo, categoriaId);
            if (filas.isEmpty()) {
                ra.addFlashAttribute("error", "El archivo no contiene filas de datos.");
                return "redirect:/importar";
            }
            session.setAttribute(SESSION_KEY, filas);

            int totalEquipos = filas.stream().mapToInt(FilaImportDTO::getCant).sum();

            model.addAttribute("categorias", servicioCategorias.listarCategoriaEquipo());
            model.addAttribute("filas", filas);
            model.addAttribute("categoriaId", categoriaId);
            model.addAttribute("totalEquipos", totalEquipos);
            return "importar/importarEquipos";

        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al procesar el archivo. Verifique que sea un Excel válido.");
            return "redirect:/importar";
        }
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/confirmar")
    public String confirmar(HttpSession session, RedirectAttributes ra) {
        List<FilaImportDTO> filas = (List<FilaImportDTO>) session.getAttribute(SESSION_KEY);
        if (filas == null || filas.isEmpty()) {
            ra.addFlashAttribute("error", "No hay datos pendientes. Suba el archivo nuevamente.");
            return "redirect:/importar";
        }

        int importados = servicioImportar.importarEquipos(filas);
        session.removeAttribute(SESSION_KEY);

        ra.addFlashAttribute("exito",
                "Importación completada: " + importados + " equipo(s) registrado(s) correctamente.");
        return "redirect:/importar";
    }

    @PostMapping("/cancelar")
    public String cancelar(HttpSession session, RedirectAttributes ra) {
        session.removeAttribute(SESSION_KEY);
        ra.addFlashAttribute("info", "Importación cancelada.");
        return "redirect:/importar";
    }
}
