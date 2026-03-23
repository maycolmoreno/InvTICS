package com.uisrael.gestionactivosapi.aplicacion.servicios;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CustodiosJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.EquiposJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.UsuariosJpa;
import com.uisrael.gestionactivosapi.presentacion.dto.response.MantenimientoManualResponseDTO;

@Service
public class PdfMantenimientoService {

    private static final String PDF_BACKGROUND_CLASSPATH = "/pdf/caratula_cresio.png";
    private static final Font TITLE = new Font(Font.TIMES_ROMAN, 11, Font.BOLD);
    private static final Font SUBTITLE = new Font(Font.TIMES_ROMAN, 9, Font.BOLD);
    private static final Font NORMAL = new Font(Font.TIMES_ROMAN, 9, Font.NORMAL);
    private static final Font ITALIC = new Font(Font.TIMES_ROMAN, 8, Font.ITALIC);
    private static final Font TABLE_HEADER = new Font(Font.TIMES_ROMAN, 7, Font.BOLD);
    private static final Font TABLE_CELL = new Font(Font.TIMES_ROMAN, 7, Font.NORMAL);
    private static final float FOTO_ANCHO = 8f * 28.3465f;
    private static final float FOTO_ALTO = 6f * 28.3465f;

    public byte[] generarInforme(MantenimientoManualResponseDTO mantenimiento, EquiposJpa equipo,
            CustodiosJpa custodio, UsuariosJpa tecnico, List<Path> imagenes) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 45, 45, 80, 60);
            PdfWriter writer = PdfWriter.getInstance(doc, out);
            configurarFondoMarca(writer);
            doc.open();

            escribirTitulo(doc, mantenimiento);
            escribirTipoMantenimiento(doc, mantenimiento);
            escribirDetalleActivo(doc, mantenimiento, equipo);
            escribirObservacion(doc, mantenimiento, imagenes);
            escribirEvidencias(doc, imagenes);
            escribirFirmas(doc, mantenimiento, custodio, tecnico);

            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("No se pudo generar el PDF del mantenimiento", e);
        }
    }

    private void escribirTitulo(Document doc, MantenimientoManualResponseDTO mantenimiento) throws Exception {
        Paragraph titulo = new Paragraph("Informe de mantenimiento preventivo/correctivo de equipos de computo", TITLE);
        titulo.setAlignment(Element.ALIGN_CENTER);
        doc.add(titulo);

        Paragraph codigo = new Paragraph(codigoInforme(mantenimiento), new Font(Font.TIMES_ROMAN, 9, Font.BOLD));
        codigo.setAlignment(Element.ALIGN_CENTER);
        codigo.setSpacingAfter(8f);
        doc.add(codigo);
        doc.add(separador());
    }

    private void escribirTipoMantenimiento(Document doc, MantenimientoManualResponseDTO mantenimiento) throws Exception {
        doc.add(new Paragraph(" Tipo de mantenimiento", SUBTITLE));

        Paragraph nota = new Paragraph("(Marcar con una X la opcion requerida)", ITALIC);
        nota.setSpacingAfter(4f);
        doc.add(nota);

        String tipoNormalizado = normalizarTipoMantenimiento(mantenimiento.getTipoMantenimiento());
        boolean preventivo = "PREVENTIVO".equals(tipoNormalizado);
        boolean correctivo = "CORRECTIVO".equals(tipoNormalizado);

        PdfPTable tipo = new PdfPTable(1);
        tipo.setWidthPercentage(50f);
        tipo.addCell(tipoCell("Tipo de mantenimiento     Seleccion", true));
        tipo.addCell(tipoCell((preventivo ? "[X]" : "[ ]") + "      Preventivo", false));
        tipo.addCell(tipoCell((correctivo ? "[X]" : "[ ]") + "      Correctivo", false));
        tipo.setSpacingAfter(8f);
        doc.add(tipo);
        doc.add(separador());
    }

    private void escribirDetalleActivo(Document doc, MantenimientoManualResponseDTO mantenimiento, EquiposJpa equipo)
            throws Exception {
        doc.add(new Paragraph(" Detalle del activo", SUBTITLE));

        Paragraph detalleSub = new Paragraph("A continuacion, se listan los bienes intervenidos.", ITALIC);
        detalleSub.setSpacingAfter(5f);
        doc.add(detalleSub);

        PdfPTable table = new PdfPTable(9);
        table.setWidthPercentage(100f);
        table.setWidths(new float[] {5, 14, 10, 8, 16, 11, 11, 11, 8});

        agregarEncabezadoTabla(table, "N\u00b0");
        agregarEncabezadoTabla(table, "CODIGO");
        agregarEncabezadoTabla(table, "CODIGO CUENTA");
        agregarEncabezadoTabla(table, "TIPO DE BIEN");
        agregarEncabezadoTabla(table, "DESCRIPCION");
        agregarEncabezadoTabla(table, "MARCA");
        agregarEncabezadoTabla(table, "SERIE");
        agregarEncabezadoTabla(table, "MODELO");
        agregarEncabezadoTabla(table, "ESTADO");

        table.addCell(tableCell("1"));
        table.addCell(tableCell(valor(equipo != null ? equipo.getCodigoSap() : mantenimiento.getEquipoCodigoSap())));
        table.addCell(tableCell("1206.001"));
        table.addCell(tableCell("ACTIVO FIJO"));
        table.addCell(tableCell(descripcionEquipo(mantenimiento, equipo)));
        table.addCell(tableCell(valor(equipo != null && equipo.getFkMarcas() != null ? equipo.getFkMarcas().getNombre() : null)));
        table.addCell(tableCell(valor(equipo != null ? equipo.getSerial() : null)));
        table.addCell(tableCell(valor(equipo != null ? equipo.getModelo() : null)));
        table.addCell(tableCell(valor(equipo != null ? equipo.getEstadoEquipo() : mantenimiento.getEstadoGeneral())));

        table.setSpacingAfter(8f);
        doc.add(table);
    }

    private void escribirObservacion(Document doc, MantenimientoManualResponseDTO mantenimiento, List<Path> imagenes)
            throws Exception {
        Paragraph observacionHeader = new Paragraph(" Diagnostico y observaciones", SUBTITLE);
        observacionHeader.setSpacingBefore(4f);
        doc.add(observacionHeader);

        PdfPTable resumen = new PdfPTable(2);
        resumen.setWidthPercentage(100f);
        resumen.setWidths(new float[] {22f, 78f});
        resumen.setSpacingBefore(4f);
        resumen.setSpacingAfter(8f);

        resumen.addCell(tipoCell("Estado general", true));
        resumen.addCell(tipoCell(valor(mantenimiento.getEstadoGeneral()), false));
        resumen.addCell(tipoCell("Detalle tecnico", true));
        resumen.addCell(tipoCell(valor(mantenimiento.getDetalle()), false));
        resumen.addCell(tipoCell("Fecha mantenimiento", true));
        resumen.addCell(tipoCell(mantenimiento.getFechaMantenimiento() != null ? mantenimiento.getFechaMantenimiento().toString() : "-", false));
        resumen.addCell(tipoCell("Proxima fecha", true));
        resumen.addCell(tipoCell(mantenimiento.getProximaFecha() != null ? mantenimiento.getProximaFecha().toString() : "No definida", false));
        resumen.addCell(tipoCell("Evidencias adjuntas", true));
        resumen.addCell(tipoCell(String.valueOf(contarEvidenciasValidas(imagenes)), false));

        doc.add(resumen);
        doc.add(separador());
    }

    private void escribirEvidencias(Document doc, List<Path> imagenes) throws Exception {
        if (imagenes == null || imagenes.isEmpty()) {
            return;
        }

        PdfPTable tablaFotos = new PdfPTable(2);
        tablaFotos.setWidthPercentage(100f);
        tablaFotos.setWidths(new float[] {50f, 50f});

        int agregadas = 0;
        for (Path path : imagenes) {
            if (agregadas >= 4) {
                break;
            }
            if (!Files.exists(path)) {
                continue;
            }
            Image img = cargarImagenValida(path);
            if (img == null) {
                continue;
            }
            img.scaleToFit(FOTO_ANCHO, FOTO_ALTO);
            PdfPCell celda = new PdfPCell();
            celda.setFixedHeight(FOTO_ALTO + 10f);
            celda.setPadding(5f);
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.addElement(img);
            tablaFotos.addCell(celda);
            agregadas++;
        }

        while (agregadas > 0 && agregadas % 2 != 0) {
            PdfPCell vacia = new PdfPCell(new Phrase(""));
            vacia.setFixedHeight(FOTO_ALTO + 10f);
            vacia.setPadding(5f);
            tablaFotos.addCell(vacia);
            agregadas++;
        }

        if (agregadas > 0) {
            tablaFotos.setSpacingAfter(8f);
            doc.add(tablaFotos);
        }
    }

    private void escribirFirmas(Document doc, MantenimientoManualResponseDTO mantenimiento, CustodiosJpa custodio,
            UsuariosJpa tecnico) throws Exception {
        Paragraph cierre = new Paragraph("Para constancia de lo antes mencionado firman la presente:", NORMAL);
        cierre.setSpacingBefore(8f);
        cierre.setSpacingAfter(8f);
        doc.add(cierre);

        PdfPTable firmas = new PdfPTable(2);
        firmas.setWidthPercentage(100f);
        firmas.setWidths(new float[] {50f, 50f});

        firmas.addCell(firmaInfo(
                "Responsable Mantenimiento",
                valor(tecnico != null ? tecnico.getNombre() : mantenimiento.getTecnicoNombre()),
                valor(tecnico != null ? tecnico.getCedula() : null),
                "Asistente de Soporte Tecnico",
                valor(tecnico != null && tecnico.getFkDepartamento() != null ? tecnico.getFkDepartamento().getNombre() : null),
                mantenimiento.getFirmaTecnico()));
        firmas.addCell(firmaInfo(
                "Custodio Asignado",
                valor(mantenimiento.getCustodioNombre()),
                valor(custodio != null ? custodio.getCedula() : null),
                valor(custodio != null && custodio.getFkCargo() != null ? custodio.getFkCargo().getNombre() : null),
                valor(custodio != null && custodio.getFkCargo() != null && custodio.getFkCargo().getFkDepartamento() != null
                        ? custodio.getFkCargo().getFkDepartamento().getNombre()
                        : null),
                mantenimiento.getFirmaCustodio()));

        firmas.setSpacingAfter(6f);
        doc.add(firmas);
    }

    private PdfPCell firmaInfo(String titulo, String nombre, String cedula, String cargo, String departamento,
            String firmaBase64) throws Exception {
        Paragraph p = new Paragraph();
        p.add(new Phrase(titulo + ":\n", SUBTITLE));
        p.add(new Phrase("Nombre: " + nombre + "\n", NORMAL));
        p.add(new Phrase("Cedula: " + cedula + "\n", NORMAL));
        p.add(new Phrase("Cargo: " + cargo + "\n", NORMAL));
        p.add(new Phrase("Departamento: " + departamento + "\n\n", NORMAL));

        PdfPCell c = new PdfPCell();
        c.addElement(p);
        c.addElement(lineaFirma(firmaBase64));
        c.setBorder(Rectangle.NO_BORDER);
        c.setPadding(4f);
        return c;
    }

    private PdfPTable lineaFirma(String base64) throws Exception {
        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(100f);
        tabla.setWidths(new float[] {18f, 82f});

        PdfPCell label = new PdfPCell(new Phrase("Firma:", NORMAL));
        label.setBorder(Rectangle.NO_BORDER);
        label.setVerticalAlignment(Element.ALIGN_BOTTOM);
        label.setPaddingTop(18f);
        label.setPaddingRight(4f);
        tabla.addCell(label);

        PdfPCell firmaCell = new PdfPCell();
        firmaCell.setBorder(Rectangle.BOTTOM);
        firmaCell.setFixedHeight(42f);
        firmaCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
        firmaCell.setPadding(0f);
        Image firma = firmaDesdeBase64(base64);
        if (firma != null) {
            firma.scaleToFit(120f, 28f);
            firma.setAlignment(Element.ALIGN_LEFT);
            firmaCell.addElement(firma);
        }
        tabla.addCell(firmaCell);
        return tabla;
    }

    private Image firmaDesdeBase64(String base64) throws Exception {
        String limpia = limpiarBase64(base64);
        if (limpia.isBlank()) {
            return null;
        }
        byte[] data = Base64.getDecoder().decode(limpia);
        return Image.getInstance(data);
    }

    private void agregarEncabezadoTabla(PdfPTable table, String texto) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, TABLE_HEADER));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(3f);
        cell.setBackgroundColor(new Color(240, 240, 240));
        table.addCell(cell);
    }

    private PdfPCell tipoCell(String text, boolean header) {
        PdfPCell c = new PdfPCell(new Phrase(text, header ? SUBTITLE : NORMAL));
        c.setPadding(4f);
        if (header) {
            c.setBackgroundColor(new Color(245, 245, 245));
        }
        return c;
    }

    private PdfPCell tableCell(String text) {
        PdfPCell c = new PdfPCell(new Phrase(text, TABLE_CELL));
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        c.setVerticalAlignment(Element.ALIGN_MIDDLE);
        c.setPadding(3f);
        return c;
    }

    private Paragraph separador() {
        Paragraph p = new Paragraph("", new Font(Font.TIMES_ROMAN, 8));
        p.setSpacingAfter(6f);
        return p;
    }

    private int contarEvidenciasValidas(List<Path> imagenes) {
        if (imagenes == null || imagenes.isEmpty()) {
            return 0;
        }
        int total = 0;
        for (Path path : imagenes) {
            if (total >= 4) {
                break;
            }
            if (!Files.exists(path)) {
                continue;
            }
            if (cargarImagenValida(path) != null) {
                total++;
            }
        }
        return total;
    }

    private String codigoInforme(MantenimientoManualResponseDTO mantenimiento) {
        String consecutivo = mantenimiento.getIdMantenimiento() == null ? "000"
                : String.format("%03d", mantenimiento.getIdMantenimiento());
        return "SIS-RPT-" + consecutivo + "-" + LocalDate.now().getYear();
    }

    private String descripcionEquipo(MantenimientoManualResponseDTO mantenimiento, EquiposJpa equipo) {
        if (equipo != null) {
            String tipo = valor(equipo.getTipoEquipo());
            String modelo = valor(equipo.getModelo());
            if (!"-".equals(tipo) && !"-".equals(modelo)) {
                return tipo + " " + modelo;
            }
            if (!"-".equals(tipo)) {
                return tipo;
            }
            if (!"-".equals(modelo)) {
                return modelo;
            }
        }
        return valor(mantenimiento.getEquipoDescripcion());
    }

    private String valor(String valor) {
        return valor == null || valor.isBlank() ? "-" : valor;
    }

    private String normalizarTipoMantenimiento(String tipoMantenimiento) {
        if (tipoMantenimiento == null || tipoMantenimiento.isBlank()) {
            return "";
        }
        String normalizado = Normalizer.normalize(tipoMantenimiento.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .replace('-', '_')
                .replace(' ', '_')
                .toUpperCase(Locale.ROOT);
        if (normalizado.contains("CORRECT")) {
            return "CORRECTIVO";
        }
        if (normalizado.contains("PREVENT")) {
            return "PREVENTIVO";
        }
        return normalizado;
    }

    private Image cargarImagenValida(Path path) {
        try {
            if (ImageIO.read(path.toFile()) == null) {
                return null;
            }
            return Image.getInstance(path.toAbsolutePath().toString());
        } catch (IOException | RuntimeException e) {
            return null;
        }
    }

    private void configurarFondoMarca(PdfWriter writer) {
        try (InputStream in = getClass().getResourceAsStream(PDF_BACKGROUND_CLASSPATH)) {
            if (in == null) {
                return;
            }
            byte[] imageBytes = in.readAllBytes();
            writer.setPageEvent(new BackgroundImageEvent(imageBytes));
        } catch (Exception ignored) {
        }
    }

    private String limpiarBase64(String base64) {
        if (base64 == null || base64.isBlank()) {
            return "";
        }
        int comma = base64.indexOf(',');
        return comma >= 0 ? base64.substring(comma + 1).trim() : base64.trim();
    }

    private static class BackgroundImageEvent extends PdfPageEventHelper {
        private final byte[] imageBytes;

        private BackgroundImageEvent(byte[] imageBytes) {
            this.imageBytes = imageBytes;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            try {
                Image bg = Image.getInstance(imageBytes);
                bg.scaleAbsolute(document.getPageSize().getWidth(), document.getPageSize().getHeight());
                bg.setAbsolutePosition(0, 0);
                PdfContentByte canvas = writer.getDirectContentUnder();
                canvas.addImage(bg);
            } catch (Exception ignored) {
            }
        }
    }
}
