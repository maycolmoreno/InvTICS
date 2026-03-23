package com.uisrael.consumogestionactivosapi.controlador;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.CategoriaEquiposRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.EquiposRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.MarcasRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.EquiposResponseDTO;
import com.uisrael.consumogestionactivosapi.service.ICategoriaEquiposServicio;
import com.uisrael.consumogestionactivosapi.service.ICustodiosServicio;
import com.uisrael.consumogestionactivosapi.service.IEquiposServicio;
import com.uisrael.consumogestionactivosapi.service.IMarcasServicio;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/equipos")
public class EquiposControlador {

	private final IEquiposServicio servicioEquipos;
	private final IMarcasServicio servicioMarcas;
	private final ICategoriaEquiposServicio servicioCategoriaEquipos;
	private final ICustodiosServicio servicioCustodios;

	@GetMapping
	public String listarEquipos(Model model) {
		List<EquiposResponseDTO> contenidoBD = servicioEquipos.listarEquipos();
		contenidoBD.sort(Comparator.comparing(EquiposResponseDTO::getIdEquipo));
		model.addAttribute("listarequipos", contenidoBD);

		return "Equipos/listarEquipos";
	}

	@GetMapping("/mantenimiento")
	public String verMantenimiento() {
		return "redirect:/mantenimiento/nuevo";
	}

	@GetMapping("/nuevo-equipo")
	public String nuevoEquipo(Model model) {

		EquiposRequestDTO equipo = new EquiposRequestDTO();
		equipo.setEstado(true);

		equipo.setFkMarca(new MarcasRequestDTO());
		equipo.getFkMarca().setIdMarca(0);

		equipo.setFkCategoria(new CategoriaEquiposRequestDTO());
		equipo.getFkCategoria().setIdCategoria(0);

		// combos
		cargarCombos(model, 0, 0);
		model.addAttribute("equipo", equipo);
		return "Equipos/nuevoEquipo";
	}

	@GetMapping("/editar-equipo/{id}")
	public String editar(@PathVariable Integer id, Model model) {
		EquiposResponseDTO dto = servicioEquipos.obtenerPorId(id);

		Integer idMarca = dto.getFkMarca().getIdMarca();

		Integer idCategoria = dto.getFkCategoria().getIdCategoria();

		model.addAttribute("listamarcas", servicioMarcas.listarMarca().stream()
				.filter(marca -> marca.isEstado() || marca.getIdMarca() == idMarca).toList());

		model.addAttribute("listacategorias", servicioCategoriaEquipos.listarCategoriaEquipo().stream()
				.filter(cate -> cate.isEstado() || cate.getIdCategoria() == idCategoria).toList());

		model.addAttribute("equipo", dto);

		return "Equipos/editarEquipo";
	}

	@PostMapping
	public String guardarEquipo(@ModelAttribute EquiposRequestDTO equipo, Model model) {

		if (equipo.getFkMarca() == null) {
			equipo.setFkMarca(new MarcasRequestDTO());
			equipo.getFkMarca().setIdMarca(0);
		}
		if (equipo.getFkCategoria() == null) {
			equipo.setFkCategoria(new CategoriaEquiposRequestDTO());
			equipo.getFkCategoria().setIdCategoria(0);
		}

		boolean hayErrores = false;

		if (equipo.getTipoEquipo() == null || equipo.getTipoEquipo().trim().isEmpty()) {
			model.addAttribute("errorTipoEquipo", "El tipo de equipo es obligatorio");
			hayErrores = true;
		}

		if (equipo.getModelo() == null || equipo.getModelo().trim().isEmpty()) {
			model.addAttribute("errorModelo", "El modelo es obligatorio");
			hayErrores = true;
		}

		if (equipo.getSerial() == null || equipo.getSerial().trim().isEmpty()) {
			model.addAttribute("errorSerial", "El serial es obligatorio");
			hayErrores = true;
		}

		if (equipo.getFkMarca().getIdMarca() <= 0) {
			model.addAttribute("errorMarca", "Debe seleccionar una marca");
			hayErrores = true;
		}
		if (equipo.getFkCategoria().getIdCategoria() <= 0) {
			model.addAttribute("errorCategoria", "Debe seleccionar una categoría");
			hayErrores = true;
		}

		boolean ipRepetida;
		if (equipo.getIp() != null && !equipo.getIp().isBlank()) {

			if (equipo.getIdEquipo() > 0) {
				// edición
				ipRepetida = servicioEquipos.existeIPParaOtro(equipo.getIp().trim(), equipo.getIdEquipo());
			} else {
				// creación
				ipRepetida = servicioEquipos.existeIP(equipo.getIp().trim());
			}

			if (ipRepetida) {
				model.addAttribute("errorIp", "Ya existe un equipo con esa dirección IP");
				hayErrores = true;
			}

			if (!esIpValida(equipo.getIp())) {
				model.addAttribute("errorIp", "La IP no tiene un formato válido");
				hayErrores = true;
			}
		}

		boolean serialRepetido;
		if (equipo.getSerial() != null && !equipo.getSerial().isBlank()) {

			if (equipo.getIdEquipo() > 0) {
				// edición
				serialRepetido = servicioEquipos.existeSerialParaOtro(equipo.getSerial().trim(), equipo.getIdEquipo());
			} else {
				// creación
				serialRepetido = servicioEquipos.existeSerial(equipo.getSerial().trim());
			}

			if (serialRepetido) {
				model.addAttribute("errorSerial", "Ya existe un equipo con ese Serial");
				hayErrores = true;
			}
		}

		boolean codigoRepetido;
		if (equipo.getCodigoSap() != null && !equipo.getCodigoSap().isBlank()) {

			if (equipo.getIdEquipo() > 0) {
				// edición
				codigoRepetido = servicioEquipos.existeCodigoParaOtro(equipo.getCodigoSap().trim(),
						equipo.getIdEquipo());
			} else {
				// creación
				codigoRepetido = servicioEquipos.existeCodigo(equipo.getCodigoSap().trim());
			}

			if (codigoRepetido) {
				model.addAttribute("errorCodigo", "Ya existe un equipo con ese Código SAP");
				hayErrores = true;
			}
		}

		boolean macRepetida;
		if (equipo.getMac() != null && !equipo.getMac().isBlank()) {

			if (equipo.getIdEquipo() > 0) {
				// edición
				macRepetida = servicioEquipos.existeMACParaOtro(equipo.getMac().trim(), equipo.getIdEquipo());
			} else {
				// creación
				macRepetida = servicioEquipos.existeMAC(equipo.getMac().trim());
			}

			if (macRepetida) {
				model.addAttribute("errorMac", "Ya existe un equipo con esa dirección MAC");
				hayErrores = true;
			}

			if (!esMacValida(equipo.getMac())) {
				model.addAttribute("errorMac", "La MAC no tiene un formato válido");
				hayErrores = true;
			}
		}

		if (hayErrores) {
			int idMarca = equipo.getFkMarca().getIdMarca();
			int idCat = equipo.getFkCategoria().getIdCategoria();

			cargarCombos(model, idMarca, idCat);
			model.addAttribute("equipo", equipo);
			return ubicacionesFormulario(equipo); // solo nuevo (si luego haces editar, se ajusta)
		}

		if (equipo.getIdEquipo() > 0) {
			servicioEquipos.actualizarEquipo(equipo.getIdEquipo(), equipo);
		} else {
			equipo.setEstado(true);
			servicioEquipos.crearEquipo(equipo);
		}

		return "redirect:/equipos";
	}

	private String ubicacionesFormulario(EquiposRequestDTO equipo) {
		return (equipo.getIdEquipo() > 0) ? "Equipos/editarEquipo" : "Equipos/nuevoEquipo";
	}

	@PostMapping("/eliminar-equipo")
	public String eliminarLogico(@RequestParam Integer idEquipo) {
		servicioEquipos.actualizarEstado(idEquipo, false);
		return "redirect:/equipos";
	}

	@PostMapping("/activar-equipo")
	public String activarEquipo(@RequestParam Integer idEquipo) {
		servicioEquipos.actualizarEstado(idEquipo, true);
		return "redirect:/equipos";
	}

	private void cargarCombos(Model model, int idMarcaSel, int idCatSel) {

		model.addAttribute("listamarcas", servicioMarcas.listarMarca().stream()
				.filter(m -> m.isEstado() || m.getIdMarca() == idMarcaSel).collect(Collectors.toList()));

		model.addAttribute("listacategorias", servicioCategoriaEquipos.listarCategoriaEquipo().stream() // ✅ tu método
																										// real
				.filter(c -> c.isEstado() || c.getIdCategoria() == idCatSel).collect(Collectors.toList()));
	}

	public static boolean esIpValida(String ip) {
		if (ip == null || ip.isBlank()) {
			return false;
		}

		String regexIp = "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}" + "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$";

		return ip.matches(regexIp);
	}

	public static boolean esMacValida(String mac) {
		if (mac == null || mac.isBlank()) {
			return false;
		}

		String regexMac = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$";

		return mac.matches(regexMac);
	}

	@GetMapping("/reporte-equipo")
	public String listarEquiposReporte(@RequestParam(required = false) String tipo, Model model) {

		List<EquiposResponseDTO> contenidoBD = servicioEquipos.listarEquipos();

		// lista de tipos únicos (antes de filtrar)
		List<String> listaTipos = contenidoBD.stream().map(EquiposResponseDTO::getTipoEquipo)
				.filter(t -> t != null && !t.trim().isEmpty()).map(String::trim).distinct()
				.sorted(String.CASE_INSENSITIVE_ORDER).toList();

		// filtrar si viene tipo
		if (tipo != null && !tipo.trim().isEmpty()) {
			String t = tipo.trim();
			contenidoBD = contenidoBD.stream()
					.filter(e -> e.getTipoEquipo() != null && e.getTipoEquipo().trim().equalsIgnoreCase(t)).toList();
		}

		contenidoBD = contenidoBD.stream().sorted(Comparator.comparing(EquiposResponseDTO::getIdEquipo)).toList();

		model.addAttribute("listarequipos", contenidoBD);
		model.addAttribute("listaTipos", listaTipos);
		model.addAttribute("tipoSeleccionado", tipo);

		return "Equipos/reporteEquipos";
	}

	@GetMapping("/reporte-equipo/excel")
	public ResponseEntity<byte[]> descargarExcelEquipos(@RequestParam(required = false) String tipo) {

		List<EquiposResponseDTO> data = servicioEquipos.listarEquipos();

		// Mismo filtro que en la vista
		if (tipo != null && !tipo.trim().isEmpty()) {
			String t = tipo.trim().toLowerCase();
			data = data.stream()
					.filter(e -> e.getTipoEquipo() != null && e.getTipoEquipo().trim().toLowerCase().equals(t))
					.toList();
		}

		String[] cols = { "ID", "CÓDIGO SAP", "TIPO", "MODELO", "SERIAL", "PROCESADOR", "RAM (GB)",
				"ALMACENAMIENTO (GB)", "SISTEMA OPERATIVO", "LIC. WINDOWS", "ETIQ. ACTIVO FIJO", "TIPO LIC. OFFICE",
				"VERSIÓN OFFICE", "UNIÓN DOMINIO", "IP", "MAC", "FECHA COMPRA", "PRECIO COMPRA", "ESTADO EQUIPO",
				"OBSERVACIÓN", "MARCA", "CATEGORÍA", "ESTADO" };

		try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			String safeSheetName = WorkbookUtil.createSafeSheetName("Reporte Equipos");
			Sheet sheet = wb.createSheet(safeSheetName);

			// =========================
			// 1) LOGO (opcional)
			// =========================
			// Pon tu logo aquí:
			// src/main/resources/static/assets/images/Logo-AMC-Oficial.png
			// Si no existe, se ignora sin romper el Excel.
			try (InputStream is = getClass().getClassLoader()
					.getResourceAsStream("static/assets/images/Logo-AMC-Oficial.png")) {

				if (is != null) {
					byte[] bytes = is.readAllBytes();
					int pictureIdx = wb.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);

					Drawing<?> drawing = sheet.createDrawingPatriarch();
					CreationHelper helper = wb.getCreationHelper();

					// 🔹 Ajusta espacio para el logo
					Row row0 = sheet.getRow(0) != null ? sheet.getRow(0) : sheet.createRow(0);
					row0.setHeightInPoints(35); // altura pequeña

					sheet.setColumnWidth(0, 14 * 256); // ancho moderado
					sheet.setColumnWidth(1, 14 * 256);

					ClientAnchor anchor = helper.createClientAnchor();
					anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_DONT_RESIZE);

					// Logo pequeño: A1 → B2
					anchor.setCol1(0); // A
					anchor.setRow1(0); // fila 1
					anchor.setCol2(2); // hasta C
					anchor.setRow2(1); // fila 2

					drawing.createPicture(anchor, pictureIdx);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			// =========================
			// 2) TÍTULO CENTRADO (merge en todas las columnas)
			// =========================
			String titulo = "REPORTE DE EQUIPOS" + ((tipo != null && !tipo.isBlank()) ? " - " + tipo.trim() : "");
			int totalCols = cols.length;

			Row titleRow = sheet.createRow(0);
			titleRow.setHeightInPoints(28);

			Cell titleCell = titleRow.createCell(0);
			titleCell.setCellValue(titulo);

			// Combinar de la columna 0 a la última
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, totalCols - 1));

			CellStyle titleStyle = wb.createCellStyle();
			Font titleFont = wb.createFont();
			titleFont.setBold(true);
			titleFont.setFontHeightInPoints((short) 14);
			titleStyle.setFont(titleFont);
			titleStyle.setAlignment(HorizontalAlignment.CENTER);
			titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			titleCell.setCellStyle(titleStyle);

			// (Opcional) Subtítulo con fecha
			Row subRow = sheet.createRow(1);
			Cell subCell = subRow.createCell(0);
			subCell.setCellValue(
					"Generado: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
			sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, totalCols - 1));

			CellStyle subStyle = wb.createCellStyle();
			Font subFont = wb.createFont();
			subFont.setItalic(true);
			subStyle.setFont(subFont);
			subStyle.setAlignment(HorizontalAlignment.CENTER);
			subStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			subCell.setCellStyle(subStyle);

			// =========================
			// 3) HEADER VERDE (fila de columnas)
			// =========================
			CellStyle headerStyle = wb.createCellStyle();
			Font headerFont = wb.createFont();
			headerFont.setBold(true);
			headerFont.setColor(IndexedColors.WHITE.getIndex());
			headerStyle.setFont(headerFont);

			headerStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
			headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			headerStyle.setAlignment(HorizontalAlignment.CENTER);
			headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

			headerStyle.setBorderBottom(BorderStyle.THIN);
			headerStyle.setBorderTop(BorderStyle.THIN);
			headerStyle.setBorderLeft(BorderStyle.THIN);
			headerStyle.setBorderRight(BorderStyle.THIN);

			// La fila de columnas será la 2 (porque 0=title, 1=subtitle)
			int headerRowIndex = 2;
			Row header = sheet.createRow(headerRowIndex);
			header.setHeightInPoints(18);

			for (int i = 0; i < cols.length; i++) {
				Cell c = header.createCell(i);
				c.setCellValue(cols[i]);
				c.setCellStyle(headerStyle);
			}

			// =========================
			// 4) DATOS (desde fila 3)
			// =========================
			CellStyle dataStyle = wb.createCellStyle();
			dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);

			int r = headerRowIndex + 1; // 3
			for (EquiposResponseDTO e : data) {
				Row row = sheet.createRow(r++);

				int c = 0;
				row.createCell(c++).setCellValue(e.getIdEquipo());

				row.createCell(c++).setCellValue(val(e.getCodigoSap()));
				row.createCell(c++).setCellValue(val(e.getTipoEquipo()));
				row.createCell(c++).setCellValue(val(e.getModelo()));
				row.createCell(c++).setCellValue(val(e.getSerial()));
				row.createCell(c++).setCellValue(val(e.getProcesador()));

				row.createCell(c++).setCellValue(numOrText(e.getMemoriaRamGb()));
				row.createCell(c++).setCellValue(numOrText(e.getCapacidadAlmacenamientoGb()));
				row.createCell(c++).setCellValue(val(e.getSistemaOperativo()));

				row.createCell(c++).setCellValue(boolSiNo(e.getLicenciaWindowsActivada()));
				row.createCell(c++).setCellValue(boolSiNo(e.getEtiquetaActivoFijo()));
				row.createCell(c++).setCellValue(val(e.getTipoLicenciaOffice()));
				row.createCell(c++).setCellValue(val(e.getVersionOffice()));
				row.createCell(c++).setCellValue(boolSiNo(e.getUnionDominio()));

				row.createCell(c++).setCellValue(val(e.getIp()));
				row.createCell(c++).setCellValue(val(e.getMac()));

				// Fecha compra (si es LocalDate / String)
				row.createCell(c++).setCellValue(formatFecha(e.getFechaCompra()));

				// Precio compra (si es BigDecimal / String)
				row.createCell(c++).setCellValue(val(e.getPrecioCompra()));

				row.createCell(c++).setCellValue(val(e.getEstadoEquipo()));
				row.createCell(c++).setCellValue(val(e.getObservacionEquipo()));

				row.createCell(c++).setCellValue(e.getFkMarca() != null ? val(e.getFkMarca().getNombre()) : "-");
				row.createCell(c++)
						.setCellValue(e.getFkCategoria() != null ? val(e.getFkCategoria().getNombre()) : "-");

				row.createCell(c++).setCellValue(e.isEstado() ? "Activo" : "Inactivo");

				// aplicar estilo a toda la fila (opcional)
				for (int i = 0; i < cols.length; i++) {
					Cell cell = row.getCell(i);
					if (cell != null) {
						cell.setCellStyle(dataStyle);
					}
				}
			}

			// =========================
			// 5) Mejoras visuales
			// =========================
			sheet.createFreezePane(0, headerRowIndex + 1); // fija hasta la fila de columnas
			sheet.setAutoFilter(new CellRangeAddress(headerRowIndex, headerRowIndex, 0, totalCols - 1));

			for (int i = 0; i < cols.length; i++) {
				sheet.autoSizeColumn(i);
			}

			wb.write(out);

			String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
			String suf = (tipo != null && !tipo.isBlank()) ? "_" + tipo.trim().toLowerCase() : "";
			String filename = "reporte_equipos" + suf + "_" + timestamp + ".xlsx";

			return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
					.contentType(MediaType
							.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
					.body(out.toByteArray());

		} catch (Exception ex) {
			throw new RuntimeException("Error generando Excel de equipos", ex);
		}
	}

	// =========================
	// Helpers
	// =========================
	private String val(Object x) {
		return (x == null) ? "-" : String.valueOf(x);
	}

	private String boolSiNo(Boolean b) {
		return (b != null && b) ? "Sí" : "No";
	}

	private String numOrText(Object n) {
		return (n == null) ? "-" : String.valueOf(n);
	}

	// Si fechaCompra viene como LocalDate, lo formatea bonito; si viene String, lo
	// deja
	private String formatFecha(Object fecha) {
		if (fecha == null) {
			return "-";
		}
		if (fecha instanceof LocalDate d) {
			return d.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}
		return String.valueOf(fecha);
	}
}
