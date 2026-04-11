package com.uisrael.consumogestionactivosapi.service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.CustodiasResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.EquiposResponseDTO;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CustodiasPdfService {

	private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final DateTimeFormatter FMT_DISPLAY = DateTimeFormatter.ofPattern("dd / MM / yyyy");
	private static final Font TITLE_FONT = new Font(Font.HELVETICA, 14, Font.BOLD);
	private static final Font SUBTITLE_FONT = new Font(Font.HELVETICA, 12, Font.BOLD);
	private static final Font NORMAL_FONT = new Font(Font.HELVETICA, 10, Font.NORMAL);
	private static final String PDF_BACKGROUND_CLASSPATH = "/pdf/marco-cresio.png";

	// Fuentes para el acta de asignación (fondo blanco, texto negro)
	private static final Font ACTA_TITLE = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
	private static final Font ACTA_SUBTITLE = new Font(Font.TIMES_ROMAN, 9, Font.BOLD);
	private static final Font ACTA_NORMAL = new Font(Font.TIMES_ROMAN, 9, Font.NORMAL);
	private static final Font ACTA_BOLD = new Font(Font.TIMES_ROMAN, 9, Font.BOLD);
	private static final Font ACTA_ITALIC = new Font(Font.TIMES_ROMAN, 9, Font.ITALIC);
	private static final Font ACTA_BOLD_ITALIC = new Font(Font.TIMES_ROMAN, 7, Font.BOLDITALIC);
	private static final Font ACTA_TABLE_HEADER = new Font(Font.TIMES_ROMAN, 7, Font.BOLD);
	private static final Font ACTA_TABLE_CELL = new Font(Font.TIMES_ROMAN, 7, Font.NORMAL);
	private static final Font ACTA_SMALL = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL);
	private static final Font ACTA_SMALL_BOLD = new Font(Font.TIMES_ROMAN, 8, Font.BOLD);
	private static final Font ACTA_META = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL, new Color(128, 128, 128));
	private static final Font ACTA_META_BOLD = new Font(Font.TIMES_ROMAN, 8, Font.BOLD, new Color(128, 128, 128));
	private static final String PDF_FONDO_ACTA = "/pdf/fondo_acta.png";
	private static final String PDF_CARATULA_CRESIO = "/pdf/caratula_cresio.png";

	public void generarActaSalidaPdf(List<CustodiasResponseDTO> lista,
			LocalDate fechaSalidaOverride, HttpServletResponse response) throws IOException {

		CustodiasResponseDTO cab = lista.get(0);

		String nombre = custodioNombre(cab);
		String cedula = custodioVal(cab, "cedula");
		Integer idCustodio = custodioId(cab);

		LocalDate fechaSalida = fechaSalidaOverride != null ? fechaSalidaOverride : LocalDate.now();

		prepararResponse(response, "Acta_Salida.pdf");

		Document doc = new Document(PageSize.A4);
		PdfWriter writer = PdfWriter.getInstance(doc, response.getOutputStream());
		configurarFondoMarca(writer);
		doc.open();

		doc.add(new Paragraph("ACTA DE SALIDA DE EQUIPOS", TITLE_FONT));
		doc.add(new Paragraph(" ", NORMAL_FONT));
		doc.add(new Paragraph("ID Custodio: " + (idCustodio != null ? idCustodio : 0), NORMAL_FONT));
		doc.add(new Paragraph("Custodio: " + nombre, NORMAL_FONT));
		doc.add(new Paragraph("C\u00e9dula: " + cedula, NORMAL_FONT));
		doc.add(new Paragraph("Fecha salida: " + fechaSalida.format(FMT), NORMAL_FONT));
		doc.add(new Paragraph("Observaci\u00f3n: " + nvl(cab.getObservacion()), NORMAL_FONT));
		doc.add(new Paragraph(" ", NORMAL_FONT));

		PdfPTable table = new PdfPTable(5);
		table.setWidthPercentage(100);
		table.addCell(headerCell("ID Equipo"));
		table.addCell(headerCell("Código"));
		table.addCell(headerCell("Modelo"));
		table.addCell(headerCell("Serial"));
		table.addCell(headerCell("Ubicaci\u00f3n"));

		Set<Integer> seen = new HashSet<>();
		for (CustodiasResponseDTO it : lista) {
			if (it.getFkEquipo() == null) continue;
			Integer idEq = it.getFkEquipo().getIdEquipo();
			if (idEq == null || !seen.add(idEq)) continue;

			table.addCell(cell(String.valueOf(idEq)));
			table.addCell(cell(nvl(it.getFkEquipo().getCodigoSap())));
			table.addCell(cell(nvl(it.getFkEquipo().getModelo())));
			table.addCell(cell(nvl(it.getFkEquipo().getSerial())));
			String ubicacion = it.getFkEquipo().getFkUbicacion() != null
					? nvl(it.getFkEquipo().getFkUbicacion().getNombre()) : "";
			table.addCell(cell(ubicacion));
		}

		doc.add(table);
		doc.add(new Paragraph(" ", NORMAL_FONT));
		doc.add(new Paragraph("Firma Custodio: __________", NORMAL_FONT));
		doc.add(new Paragraph("Firma Responsable TI: ________", NORMAL_FONT));
		doc.close();
	}

	public void generarActaEntregaPdf(List<CustodiasResponseDTO> lista,
			String nombreEntrega, String deptoEntrega, String tipoMovimiento,
			HttpServletResponse response) throws IOException {
		prepararResponse(response, "Acta_Asignacion.pdf");
		escribirActaEntregaPdf(lista, nombreEntrega, deptoEntrega, tipoMovimiento, response.getOutputStream());
	}

	private void escribirActaEntregaPdf(List<CustodiasResponseDTO> lista,
			String nombreEntrega, String deptoEntrega, String tipoMovimiento,
			OutputStream os) throws IOException {

		CustodiasResponseDTO cab = lista.get(0);

		String nombre = custodioNombre(cab);
		String cedula = custodioVal(cab, "cedula");
		String cargo = cab.getFkCustodio() != null && cab.getFkCustodio().getFkCargo() != null
				? nvl(cab.getFkCustodio().getFkCargo().getNombre()) : "";
		String departamento = cab.getFkCustodio() != null && cab.getFkCustodio().getFkDepartamento() != null
				? nvl(cab.getFkCustodio().getFkDepartamento().getNombre()) : "";
		String sucursal = cab.getFkCustodio() != null && cab.getFkCustodio().getFkUbicacion() != null
				? nvl(cab.getFkCustodio().getFkUbicacion().getNombre()) : "";
		LocalDate fechaInicio = fechaValida(cab.getFechaInicio());
		String fechaTexto = fechaInicio != null ? fechaInicio.format(FMT_DISPLAY) : LocalDate.now().format(FMT_DISPLAY);

		// Numero de acta
		Integer idCustodio = custodioId(cab);
		String numActa = String.format("%09d", idCustodio != null ? idCustodio : 0);

		// Prefijo y etiqueta según tipo de movimiento
		if (tipoMovimiento == null || tipoMovimiento.isBlank()) tipoMovimiento = "ASIGNACION";
		String etiquetaTipo = switch (tipoMovimiento) {
			case "ACTA_INICIAL" -> "Acta Inicial";
			case "TRASLADO"     -> "Traslado";
			case "BAJA"         -> "Baja";
			default             -> "Acta Asignación";
		};
		String prefijo = switch (tipoMovimiento) {
			case "ACTA_INICIAL" -> "FM001-001-TIC-\u2192 Acta Inicial N.\u00ba ";
			case "TRASLADO"     -> "FM001-001-TIC-\u2192 Traslado N.\u00ba ";
			case "BAJA"         -> "FM001-001-TIC-\u2192 Baja N.\u00ba ";
			default             -> "FM001-001-TIC-\u2192 Acta Asignación N.\u00ba ";
		};

		Document doc = new Document(PageSize.A4, 55, 55, 100, 80);
		PdfWriter writer = PdfWriter.getInstance(doc, os);
		configurarCaratula(writer);
		doc.open();

		// === TITULO CENTRAL ===
		Paragraph titulo = new Paragraph("Actas de Responsabilidad de Activos Fijos", ACTA_TITLE);
		titulo.setAlignment(Element.ALIGN_CENTER);
		doc.add(titulo);

		Paragraph subTitulo = new Paragraph(prefijo + numActa, ACTA_SUBTITLE);
		subTitulo.setAlignment(Element.ALIGN_CENTER);
		doc.add(subTitulo);

		doc.add(new Paragraph(" ", new Font(Font.TIMES_ROMAN, 8)));

		// === INFORMACIÓN GENERAL ===
		Paragraph infoHeader = new Paragraph("Información General", ACTA_TITLE);
		infoHeader.setIndentationLeft(15);
		doc.add(infoHeader);

		doc.add(new Paragraph(" ", new Font(Font.TIMES_ROMAN, 4)));

		addBulletField(doc, "Nombre del responsable:", nombre);
		addBulletField(doc, "Cargo:", cargo);
		addBulletField(doc, "Sucursal:", sucursal);
		addBulletField(doc, "Área o Departamento:", departamento);
		addBulletField(doc, "Cédula de Identidad:", cedula);
		addBulletField(doc, "Fecha de Elaboración del Acta:", fechaTexto);

		doc.add(new Paragraph(" ", new Font(Font.TIMES_ROMAN, 8)));

		// === TIPO DE ACTA ===
		Paragraph tipoHeader = new Paragraph("Tipo de Acta", ACTA_SUBTITLE);
		tipoHeader.setIndentationLeft(5);
		doc.add(tipoHeader);
		Paragraph tipoItalic = new Paragraph("(Marcar con una X la opción requerida)", ACTA_ITALIC);
		tipoItalic.setIndentationLeft(5);
		doc.add(tipoItalic);

		doc.add(new Paragraph(" ", new Font(Font.TIMES_ROMAN, 4)));

		PdfPTable tipoTable = new PdfPTable(2);
		tipoTable.setWidthPercentage(30);
		tipoTable.setHorizontalAlignment(Element.ALIGN_LEFT);
		tipoTable.setWidths(new float[]{60, 40});
		tipoTable.addCell(tipoCellBold("Tipo de Acta"));
		tipoTable.addCell(tipoCellBold("Selección"));
		tipoTable.addCell(tipoCell("Acta Inicial"));
		tipoTable.addCell(tipoCell("ACTA_INICIAL".equals(tipoMovimiento) ? "[X]" : "[ ]"));
		tipoTable.addCell(tipoCell("Acta Asignación"));
		tipoTable.addCell(tipoCell("ASIGNACION".equals(tipoMovimiento) ? "[X]" : "[ ]"));
		tipoTable.addCell(tipoCell("Traslado"));
		tipoTable.addCell(tipoCell("TRASLADO".equals(tipoMovimiento) ? "[X]" : "[ ]"));
		tipoTable.addCell(tipoCell("Baja"));
		tipoTable.addCell(tipoCell("BAJA".equals(tipoMovimiento) ? "[X]" : "[ ]"));
		doc.add(tipoTable);

		doc.add(new Paragraph(" ", new Font(Font.TIMES_ROMAN, 6)));

		// === DETALLE DEL ACTIVO ===
		Paragraph detalleHeader = new Paragraph(" Detalle del Activo(s)", ACTA_SUBTITLE);
		detalleHeader.setIndentationLeft(5);
		doc.add(detalleHeader);
		Paragraph detalleSub = new Paragraph("A continuación, se listan los bienes entregados", ACTA_NORMAL);
		detalleSub.setIndentationLeft(5);
		doc.add(detalleSub);

		doc.add(new Paragraph(" ", new Font(Font.TIMES_ROMAN, 4)));

		// Tabla de equipos
		PdfPTable table = new PdfPTable(10);
		table.setWidthPercentage(100);
		table.setWidths(new float[]{4, 12, 9, 7, 12, 9, 10, 10, 10, 9});

		String[] headers = {"N°", "CODIGO", "CODIGO\nCUENTA", "TIPO DE\nBIEN",
				"DESCRIPCION", "MARCA", "SERIE", "MODELO", "UBICACION", "ESTADO"};
		for (String h : headers) {
			PdfPCell hc = new PdfPCell(new Phrase(h, ACTA_TABLE_HEADER));
			hc.setHorizontalAlignment(Element.ALIGN_CENTER);
			hc.setVerticalAlignment(Element.ALIGN_MIDDLE);
			hc.setPadding(3);
			hc.setBackgroundColor(new Color(240, 240, 240));
			table.addCell(hc);
		}

		Set<Integer> seen = new HashSet<>();
		int contador = 1;
		for (CustodiasResponseDTO it : lista) {
			if (it.getFkEquipo() == null) continue;
			Integer idEq = it.getFkEquipo().getIdEquipo();
			if (idEq == null || !seen.add(idEq)) continue;

			table.addCell(tableCell(String.valueOf(contador++)));
			table.addCell(tableCell(nvl(it.getFkEquipo().getCodigoSap())));
			table.addCell(tableCell("1206.001"));
			table.addCell(tableCell("ACTIVO FIJO"));
			String descripcion = it.getFkEquipo().getFkCategoria() != null
					? nvl(it.getFkEquipo().getFkCategoria().getNombre()) : "N/A";
			table.addCell(tableCell(descripcion.isEmpty() ? "N/A" : descripcion));
			String marca = it.getFkEquipo().getFkMarca() != null
					? nvl(it.getFkEquipo().getFkMarca().getNombre()) : "N/A";
			table.addCell(tableCell(marca.isEmpty() ? "N/A" : marca));
			String serie = nvl(it.getFkEquipo().getSerial());
			table.addCell(tableCell(serie.isEmpty() ? "N/A" : serie));
			String modelo = nvl(it.getFkEquipo().getModelo());
			table.addCell(tableCell(modelo.isEmpty() ? "N/A" : modelo));
			String ubicacion = it.getFkEquipo().getFkUbicacion() != null
					? nvl(it.getFkEquipo().getFkUbicacion().getNombre()) : "N/A";
			table.addCell(tableCell(ubicacion.isEmpty() ? "N/A" : ubicacion));
			table.addCell(tableCell(nvl(it.getFkEquipo().getEstadoEquipo()).isEmpty()
					? "BUENO" : it.getFkEquipo().getEstadoEquipo()));
		}

		doc.add(table);

		doc.add(new Paragraph(" ", new Font(Font.TIMES_ROMAN, 4)));

		// Cantidad
		Paragraph cantP = new Paragraph();
		cantP.add(new Phrase("Cantidad de bienes entregados", ACTA_BOLD));
		cantP.add(new Phrase(": " + (contador - 1), ACTA_NORMAL));
		cantP.setIndentationLeft(5);
		doc.add(cantP);

		doc.add(new Paragraph(" ", new Font(Font.TIMES_ROMAN, 4)));

		// Disclaimers
		doc.add(new Paragraph(
				"ES DE TOTAL RESPONSABILIDAD LA CUSTODIA Y BUEN USO DE LOS BIENES DETALLADOS, EN CASO DE INCUMPLIMIENTO SE APLICARÁ LAS SANCIONES CORRESPONDIENTE CONFORME EL REGLAMENTO INTERNO DE TRABAJO.",
				ACTA_BOLD_ITALIC));
		doc.add(new Paragraph(" ", new Font(Font.TIMES_ROMAN, 3)));
		doc.add(new Paragraph(
				"NOTA: LOS BIENES PARA SER TRASLADADOS A OTRA UBICACIÓN DEBEN CONTAR CON LA AUTORIZACIÓN DEL CUSTODIO Y EL EQUIPO DE CAPEX.",
				ACTA_BOLD_ITALIC));

		doc.add(new Paragraph(" ", new Font(Font.TIMES_ROMAN, 10)));

		// === FIRMAS ===
		Paragraph firmaIntro = new Paragraph("Para constancia de lo antes mencionado firman la presente:", ACTA_SMALL);
		firmaIntro.setIndentationLeft(5);
		doc.add(firmaIntro);

		doc.add(new Paragraph(" ", new Font(Font.TIMES_ROMAN, 8)));

		// Nombre entrega  (quien genera el acta / custodio origen en traslado)
		String nomEntrega;
		String deptEntrega;
		if ("TRASLADO".equals(tipoMovimiento)) {
			nomEntrega = (nombreEntrega == null || nombreEntrega.isBlank()) ? "" : nombreEntrega;
			deptEntrega = (deptoEntrega == null || deptoEntrega.isBlank()) ? "" : deptoEntrega;
		} else {
			nomEntrega = (nombreEntrega == null || nombreEntrega.isBlank()) ? "Equipo de TI" : nombreEntrega;
			deptEntrega = (deptoEntrega == null || deptoEntrega.isBlank()) ? "Transformación Digital" : deptoEntrega;
		}

		PdfPTable firmaTable = new PdfPTable(2);
		firmaTable.setWidthPercentage(100);
		firmaTable.setWidths(new float[]{50, 50});

		// Fila: Responsable que entrega / recibe
		firmaTable.addCell(firmaCellBold("Responsable que entrega:"));
		firmaTable.addCell(firmaCellBold("Responsable que recibe:"));

		// Nombre
		firmaTable.addCell(firmaCellLabel("Nombre", nomEntrega));
		firmaTable.addCell(firmaCellLabel("Nombre", nombre));

		// Departamento
		firmaTable.addCell(firmaCellLabel("Departamento", deptEntrega));
		firmaTable.addCell(firmaCellLabel("Departamento", departamento));

		// Espacio para firma
		firmaTable.addCell(firmaCell(" "));
		firmaTable.addCell(firmaCell(" "));
		firmaTable.addCell(firmaCell("Firma: __________________________"));
		firmaTable.addCell(firmaCell("Firma: __________________________"));

		doc.add(firmaTable);

		doc.close();
	}

	/**
	 * Genera el PDF del acta de entrega/asignación y retorna los bytes.
	 * Útil para adjuntar al correo electrónico.
	 */
	public byte[] generarActaEntregaPdfBytes(List<CustodiasResponseDTO> lista,
			String nombreEntrega, String deptoEntrega, String tipoMovimiento) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		escribirActaEntregaPdf(lista, nombreEntrega, deptoEntrega, tipoMovimiento, baos);
		return baos.toByteArray();
	}

	/**
	 * Genera el PDF del informe de mantenimiento con el mismo formato base
	 * del acta de asignacion, pero sin la seccion "Tipo de Acta". Incluye
	 * observacion e imagenes en el documento.
	 */
	public byte[] generarMantenimientoPdfBytes(List<CustodiasResponseDTO> lista,
			LocalDate fechaMantenimiento, String observacion, List<MultipartFile> imagenes) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		escribirMantenimientoPdf(lista, fechaMantenimiento, observacion, imagenes, baos);
		return baos.toByteArray();
	}

	private void escribirMantenimientoPdf(List<CustodiasResponseDTO> lista,
			LocalDate fechaMantenimiento, String observacion, List<MultipartFile> imagenes,
			OutputStream os) throws IOException {

		CustodiasResponseDTO cab = lista.get(0);

		String nombre = custodioNombre(cab);
		String cedula = custodioVal(cab, "cedula");
		String cargo = cab.getFkCustodio() != null && cab.getFkCustodio().getFkCargo() != null
				? nvl(cab.getFkCustodio().getFkCargo().getNombre()) : "";
		String departamento = cab.getFkCustodio() != null && cab.getFkCustodio().getFkDepartamento() != null
				? nvl(cab.getFkCustodio().getFkDepartamento().getNombre()) : "";
		String sucursal = cab.getFkCustodio() != null && cab.getFkCustodio().getFkUbicacion() != null
				? nvl(cab.getFkCustodio().getFkUbicacion().getNombre()) : "";
		LocalDate fechaBase = fechaMantenimiento != null ? fechaMantenimiento : LocalDate.now();
		String fechaTexto = fechaBase.format(FMT_DISPLAY);

		Integer idCustodio = custodioId(cab);
		String numActa = String.format("%09d", idCustodio != null ? idCustodio : 0);
		String prefijo = "FM001-001-TIC-\u2192 Informe Mantenimiento N.\u00ba ";

		Document doc = new Document(PageSize.A4, 55, 55, 100, 80);
		PdfWriter writer = PdfWriter.getInstance(doc, os);
		configurarCaratula(writer);
		doc.open();

		Paragraph titulo = new Paragraph("Informe de Mantenimiento de Activos Fijos", ACTA_TITLE);
		titulo.setAlignment(Element.ALIGN_CENTER);
		doc.add(titulo);

		Paragraph subTitulo = new Paragraph(prefijo + numActa, ACTA_SUBTITLE);
		subTitulo.setAlignment(Element.ALIGN_CENTER);
		doc.add(subTitulo);

		doc.add(new Paragraph(" ", new Font(Font.TIMES_ROMAN, 8)));

		Paragraph infoHeader = new Paragraph("Informaci\u00f3n General", ACTA_TITLE);
		infoHeader.setIndentationLeft(15);
		doc.add(infoHeader);

		doc.add(new Paragraph(" ", new Font(Font.TIMES_ROMAN, 4)));

		addBulletField(doc, "Nombre del responsable:", nombre);
		addBulletField(doc, "Cargo:", cargo);
		addBulletField(doc, "Sucursal:", sucursal);
		addBulletField(doc, "\u00c1rea o Departamento:", departamento);
		addBulletField(doc, "C\u00e9dula de Identidad:", cedula);
		addBulletField(doc, "Fecha de Mantenimiento:", fechaTexto);

		doc.add(new Paragraph(" ", new Font(Font.TIMES_ROMAN, 6)));

		Paragraph detalleHeader = new Paragraph(" Detalle del Activo(s)", ACTA_SUBTITLE);
		detalleHeader.setIndentationLeft(5);
		doc.add(detalleHeader);
		Paragraph detalleSub = new Paragraph("A continuaci\u00f3n, se listan los bienes entregados", ACTA_NORMAL);
		detalleSub.setIndentationLeft(5);
		doc.add(detalleSub);

		doc.add(new Paragraph(" ", new Font(Font.TIMES_ROMAN, 4)));

		PdfPTable table = new PdfPTable(10);
		table.setWidthPercentage(100);
		table.setWidths(new float[]{4, 12, 9, 7, 12, 9, 10, 10, 10, 9});

		String[] headers = {"N\u00b0", "CODIGO", "CODIGO\nCUENTA", "TIPO DE\nBIEN",
				"DESCRIPCION", "MARCA", "SERIE", "MODELO", "UBICACION", "ESTADO"};
		for (String h : headers) {
			PdfPCell hc = new PdfPCell(new Phrase(h, ACTA_TABLE_HEADER));
			hc.setHorizontalAlignment(Element.ALIGN_CENTER);
			hc.setVerticalAlignment(Element.ALIGN_MIDDLE);
			hc.setPadding(3);
			hc.setBackgroundColor(new Color(240, 240, 240));
			table.addCell(hc);
		}

		Set<Integer> seen = new HashSet<>();
		int contador = 1;
		for (CustodiasResponseDTO it : lista) {
			if (it.getFkEquipo() == null) continue;
			Integer idEq = it.getFkEquipo().getIdEquipo();
			if (idEq == null || !seen.add(idEq)) continue;

			table.addCell(tableCell(String.valueOf(contador++)));
			table.addCell(tableCell(nvl(it.getFkEquipo().getCodigoSap())));
			table.addCell(tableCell("1206.001"));
			table.addCell(tableCell("ACTIVO FIJO"));
			String descripcion = it.getFkEquipo().getFkCategoria() != null
					? nvl(it.getFkEquipo().getFkCategoria().getNombre()) : "N/A";
			table.addCell(tableCell(descripcion.isEmpty() ? "N/A" : descripcion));
			String marca = it.getFkEquipo().getFkMarca() != null
					? nvl(it.getFkEquipo().getFkMarca().getNombre()) : "N/A";
			table.addCell(tableCell(marca.isEmpty() ? "N/A" : marca));
			String serie = nvl(it.getFkEquipo().getSerial());
			table.addCell(tableCell(serie.isEmpty() ? "N/A" : serie));
			String modelo = nvl(it.getFkEquipo().getModelo());
			table.addCell(tableCell(modelo.isEmpty() ? "N/A" : modelo));
			String ubicacion = it.getFkEquipo().getFkUbicacion() != null
					? nvl(it.getFkEquipo().getFkUbicacion().getNombre()) : "N/A";
			table.addCell(tableCell(ubicacion.isEmpty() ? "N/A" : ubicacion));
			table.addCell(tableCell(nvl(it.getFkEquipo().getEstadoEquipo()).isEmpty()
					? "BUENO" : it.getFkEquipo().getEstadoEquipo()));
		}

		doc.add(table);
		doc.add(new Paragraph(" ", new Font(Font.TIMES_ROMAN, 6)));

		Paragraph obsHeader = new Paragraph(" Observaci\u00f3n de mantenimiento", ACTA_SUBTITLE);
		obsHeader.setIndentationLeft(5);
		doc.add(obsHeader);
		String obsTexto = (observacion == null || observacion.isBlank()) ? "-" : observacion;
		Paragraph obs = new Paragraph(obsTexto, ACTA_NORMAL);
		obs.setIndentationLeft(10);
		doc.add(obs);

		if (imagenes != null && !imagenes.isEmpty()) {
			doc.add(new Paragraph(" ", new Font(Font.TIMES_ROMAN, 6)));
			Paragraph imgHeader = new Paragraph(" Imagenes de mantenimiento", ACTA_SUBTITLE);
			imgHeader.setIndentationLeft(5);
			doc.add(imgHeader);

			for (MultipartFile img : imagenes) {
				if (img == null || img.isEmpty()) continue;
				String contentType = img.getContentType();
				if (contentType != null && !contentType.startsWith("image/")) continue;
				try {
					Image image = Image.getInstance(img.getBytes());
					image.scaleToFit(450, 300);
					image.setAlignment(Element.ALIGN_CENTER);
					doc.add(image);
					doc.add(new Paragraph(" ", new Font(Font.TIMES_ROMAN, 4)));
				} catch (Exception ignored) {
					// Si una imagen falla, continuar con las demas.
				}
			}
		}

		doc.close();
	}

	public void generarActaBajaPdf(List<EquiposResponseDTO> equipos,
			LocalDate fechaBaja, String observacion, HttpServletResponse response) throws IOException {

		LocalDate fecha = fechaBaja != null ? fechaBaja : LocalDate.now();
		String obs = (observacion == null || observacion.isBlank()) ? "Baja de activo" : observacion;

		prepararResponse(response, "Acta_Baja.pdf");

		Document doc = new Document(PageSize.A4);
		PdfWriter writer = PdfWriter.getInstance(doc, response.getOutputStream());
		configurarFondoMarca(writer);
		doc.open();

		doc.add(new Paragraph("ACTA DE BAJA DE ACTIVOS FIJOS", TITLE_FONT));
		doc.add(new Paragraph(" ", NORMAL_FONT));
		doc.add(new Paragraph("Fecha de baja: " + fecha.format(FMT), NORMAL_FONT));
		doc.add(new Paragraph("Motivo / Observacion: " + obs, NORMAL_FONT));
		doc.add(new Paragraph(" ", NORMAL_FONT));

		PdfPTable table = new PdfPTable(6);
		table.setWidthPercentage(100);
		table.addCell(headerCell("ID"));
		table.addCell(headerCell("Codigo"));
		table.addCell(headerCell("Modelo"));
		table.addCell(headerCell("Serial"));
		table.addCell(headerCell("Ubicaci\u00f3n"));
		table.addCell(headerCell("Estado"));

		for (EquiposResponseDTO e : equipos) {
			if (e == null) continue;
			table.addCell(cell(String.valueOf(e.getIdEquipo())));
			table.addCell(cell(nvl(e.getCodigoSap())));
			table.addCell(cell(nvl(e.getModelo())));
			table.addCell(cell(nvl(e.getSerial())));
			String ubicacion = e.getFkUbicacion() != null ? nvl(e.getFkUbicacion().getNombre()) : "";
			table.addCell(cell(ubicacion));
			table.addCell(cell("BAJA"));
		}

		doc.add(table);
		doc.add(new Paragraph(" ", NORMAL_FONT));
		doc.add(new Paragraph("Firma Responsable TI: ____________________", NORMAL_FONT));
		doc.add(new Paragraph("Firma Solicitante: _______________________", NORMAL_FONT));
		doc.close();
	}

	// === Helpers compartidos ===

	private void prepararResponse(HttpServletResponse response, String filename) {
		response.reset();
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=" + filename);
	}

	private PdfPCell headerCell(String text) {
		PdfPCell c = new PdfPCell(new Phrase(text));
		c.setPadding(5);
		return c;
	}

	private PdfPCell cell(String text) {
		PdfPCell c = new PdfPCell(new Phrase(text));
		c.setPadding(5);
		return c;
	}

	private String nvl(String s) {
		return s == null ? "" : s;
	}

	private String custodioNombre(CustodiasResponseDTO cab) {
		return cab.getFkCustodio() != null ? nvl(cab.getFkCustodio().getNombre()) : "";
	}

	private String custodioVal(CustodiasResponseDTO cab, String campo) {
		if (cab.getFkCustodio() == null) return "";
		return "cedula".equals(campo) ? nvl(cab.getFkCustodio().getCedula()) : "";
	}

	private Integer custodioId(CustodiasResponseDTO cab) {
		return cab.getFkCustodio() != null ? cab.getFkCustodio().getIdCustodio() : null;
	}

	private LocalDate fechaValida(LocalDate fecha) {
		if (fecha == null) return null;
		return fecha.getYear() < 1900 ? null : fecha;
	}

	private void configurarFondoMarca(PdfWriter writer) {
		try (InputStream in = getClass().getResourceAsStream(PDF_BACKGROUND_CLASSPATH)) {
			if (in == null) return;
			byte[] imageBytes = in.readAllBytes();
			writer.setPageEvent(new BackgroundImageEvent(imageBytes));
		} catch (Exception ignored) {
			// Si no existe la imagen, el PDF sigue generandose sin fondo.
		}
	}

	private void configurarFondoActa(PdfWriter writer) {
		try (InputStream in = getClass().getResourceAsStream(PDF_FONDO_ACTA)) {
			if (in == null) return;
			byte[] imageBytes = in.readAllBytes();
			writer.setPageEvent(new BackgroundImageEvent(imageBytes));
		} catch (Exception ignored) {
			// Si no existe la imagen, el PDF sigue generandose sin fondo.
		}
	}

	private void configurarCaratula(PdfWriter writer) {
		try (InputStream in = getClass().getResourceAsStream(PDF_CARATULA_CRESIO)) {
			if (in == null) return;
			byte[] imageBytes = in.readAllBytes();
			writer.setPageEvent(new BackgroundImageEvent(imageBytes));
		} catch (Exception ignored) {
		}
	}

	// --- Helpers para el acta de asignación ---

	private PdfPCell metaCell(String text, boolean bold) {
		PdfPCell c = new PdfPCell(new Phrase(text, bold ? ACTA_META_BOLD : ACTA_META));
		c.setBorder(Rectangle.NO_BORDER);
		c.setPadding(1);
		return c;
	}

	private void addBulletField(Document doc, String label, String value) throws com.lowagie.text.DocumentException {
		Paragraph p = new Paragraph();
		p.add(new Phrase("    \u2022 ", ACTA_NORMAL));
		p.add(new Phrase(label + " ", ACTA_BOLD));
		p.add(new Phrase(value, ACTA_NORMAL));
		p.setIndentationLeft(20);
		doc.add(p);
	}

	private PdfPCell tipoCellBold(String text) {
		PdfPCell c = new PdfPCell(new Phrase(text, ACTA_TABLE_HEADER));
		c.setHorizontalAlignment(Element.ALIGN_CENTER);
		c.setPadding(3);
		c.setBackgroundColor(new Color(240, 240, 240));
		return c;
	}

	private PdfPCell tipoCell(String text) {
		PdfPCell c = new PdfPCell(new Phrase(text, ACTA_TABLE_CELL));
		c.setHorizontalAlignment(Element.ALIGN_CENTER);
		c.setPadding(3);
		return c;
	}

	private PdfPCell tableCell(String text) {
		PdfPCell c = new PdfPCell(new Phrase(text, ACTA_TABLE_CELL));
		c.setHorizontalAlignment(Element.ALIGN_CENTER);
		c.setVerticalAlignment(Element.ALIGN_MIDDLE);
		c.setPadding(2);
		return c;
	}

	private PdfPCell firmaCellBold(String text) {
		PdfPCell c = new PdfPCell(new Phrase(text, ACTA_SMALL_BOLD));
		c.setBorder(Rectangle.NO_BORDER);
		c.setPadding(2);
		c.setHorizontalAlignment(Element.ALIGN_CENTER);
		return c;
	}

	private PdfPCell firmaCellLabel(String label, String value) {
		Phrase p = new Phrase();
		p.add(new Phrase(label + ": ", ACTA_SMALL_BOLD));
		p.add(new Phrase(value, ACTA_SMALL));
		PdfPCell c = new PdfPCell(p);
		c.setBorder(Rectangle.NO_BORDER);
		c.setPadding(2);
		c.setHorizontalAlignment(Element.ALIGN_CENTER);
		return c;
	}

	private PdfPCell firmaCell(String text) {
		PdfPCell c = new PdfPCell(new Phrase(text, ACTA_SMALL));
		c.setBorder(Rectangle.NO_BORDER);
		c.setPadding(2);
		c.setHorizontalAlignment(Element.ALIGN_CENTER);
		return c;
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
				bg.setAbsolutePosition(0, 0);
				bg.scaleAbsolute(document.getPageSize().getWidth(), document.getPageSize().getHeight());
				writer.getDirectContentUnder().addImage(bg);
			} catch (Exception ignored) {
				// No bloquear la generacion del PDF por errores de fondo.
			}
		}
	}
}
