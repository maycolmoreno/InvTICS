package com.uisrael.consumogestionactivosapi.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
import org.springframework.stereotype.Service;

import com.uisrael.consumogestionactivosapi.modelo.dto.response.EquiposResponseDTO;

@Service
public class EquiposExcelService {

	private static final String[] COLS = { "ID", "CÓDIGO SAP", "MODELO", "SERIAL", "PROCESADOR", "RAM (GB)",
			"ALMACENAMIENTO (GB)", "LIC. WINDOWS", "MAC", "FECHA COMPRA", "PRECIO COMPRA", "ESTADO EQUIPO",
			"OBSERVACIÓN", "MARCA", "CATEGORÍA", "ESTADO" };

	public byte[] generarReporteExcel(List<EquiposResponseDTO> data, String tipo) {

		try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			String safeSheetName = WorkbookUtil.createSafeSheetName("Reporte Equipos");
			Sheet sheet = wb.createSheet(safeSheetName);
			int totalCols = COLS.length;

			// 1) LOGO (opcional)
			insertarLogo(wb, sheet);

			// 2) TÍTULO CENTRADO
			String titulo = "REPORTE DE EQUIPOS" + ((tipo != null && !tipo.isBlank()) ? " - " + tipo.trim() : "");
			crearTitulo(wb, sheet, totalCols, titulo);

			// Subtítulo con fecha
			crearSubtitulo(wb, sheet, totalCols);

			// 3) HEADER VERDE
			CellStyle headerStyle = crearEstiloHeader(wb);
			int headerRowIndex = 2;
			Row header = sheet.createRow(headerRowIndex);
			header.setHeightInPoints(18);
			for (int i = 0; i < COLS.length; i++) {
				Cell c = header.createCell(i);
				c.setCellValue(COLS[i]);
				c.setCellStyle(headerStyle);
			}

			// 4) DATOS
			CellStyle dataStyle = wb.createCellStyle();
			dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);

			int r = headerRowIndex + 1;
			for (EquiposResponseDTO e : data) {
				Row row = sheet.createRow(r++);

				int c = 0;
				row.createCell(c++).setCellValue(e.getIdEquipo());
				row.createCell(c++).setCellValue(val(e.getCodigoSap()));
				row.createCell(c++).setCellValue(val(e.getModelo()));
				row.createCell(c++).setCellValue(val(e.getSerial()));
				row.createCell(c++).setCellValue(val(e.getProcesador()));
				row.createCell(c++).setCellValue(numOrText(e.getMemoriaRamGb()));
				row.createCell(c++).setCellValue(numOrText(e.getCapacidadAlmacenamientoGb()));
				row.createCell(c++).setCellValue(boolSiNo(e.getLicenciaWindowsActivada()));
				row.createCell(c++).setCellValue(val(e.getMac()));
				row.createCell(c++).setCellValue(formatFecha(e.getFechaCompra()));
				row.createCell(c++).setCellValue(val(e.getPrecioCompra()));
				row.createCell(c++).setCellValue(val(e.getEstadoEquipo()));
				row.createCell(c++).setCellValue(val(e.getObservacionEquipo()));
				row.createCell(c++).setCellValue(e.getFkMarca() != null ? val(e.getFkMarca().getNombre()) : "-");
				row.createCell(c++).setCellValue(e.getFkCategoria() != null ? val(e.getFkCategoria().getNombre()) : "-");
				row.createCell(c++).setCellValue(e.isEstado() ? "Activo" : "Inactivo");

				for (int i = 0; i < COLS.length; i++) {
					Cell cell = row.getCell(i);
					if (cell != null) {
						cell.setCellStyle(dataStyle);
					}
				}
			}

			// 5) Mejoras visuales
			sheet.createFreezePane(0, headerRowIndex + 1);
			sheet.setAutoFilter(new CellRangeAddress(headerRowIndex, headerRowIndex, 0, totalCols - 1));
			for (int i = 0; i < COLS.length; i++) {
				sheet.autoSizeColumn(i);
			}

			wb.write(out);
			return out.toByteArray();

		} catch (Exception ex) {
			throw new RuntimeException("Error generando Excel de equipos", ex);
		}
	}

	// ==================== helpers ====================

	private void insertarLogo(Workbook wb, Sheet sheet) {
		try (InputStream is = getClass().getClassLoader()
				.getResourceAsStream("static/assets/images/Logo-AMC-Oficial.png")) {
			if (is != null) {
				byte[] bytes = is.readAllBytes();
				int pictureIdx = wb.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
				Drawing<?> drawing = sheet.createDrawingPatriarch();
				CreationHelper helper = wb.getCreationHelper();

				Row row0 = sheet.getRow(0) != null ? sheet.getRow(0) : sheet.createRow(0);
				row0.setHeightInPoints(35);
				sheet.setColumnWidth(0, 14 * 256);
				sheet.setColumnWidth(1, 14 * 256);

				ClientAnchor anchor = helper.createClientAnchor();
				anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_DONT_RESIZE);
				anchor.setCol1(0);
				anchor.setRow1(0);
				anchor.setCol2(2);
				anchor.setRow2(1);
				drawing.createPicture(anchor, pictureIdx);
			}
		} catch (Exception e) {
			// Logo no encontrado – continúa sin logo
		}
	}

	private void crearTitulo(Workbook wb, Sheet sheet, int totalCols, String titulo) {
		Row titleRow = sheet.createRow(0);
		titleRow.setHeightInPoints(28);
		Cell titleCell = titleRow.createCell(0);
		titleCell.setCellValue(titulo);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, totalCols - 1));

		CellStyle titleStyle = wb.createCellStyle();
		Font titleFont = wb.createFont();
		titleFont.setBold(true);
		titleFont.setFontHeightInPoints((short) 14);
		titleStyle.setFont(titleFont);
		titleStyle.setAlignment(HorizontalAlignment.CENTER);
		titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		titleCell.setCellStyle(titleStyle);
	}

	private void crearSubtitulo(Workbook wb, Sheet sheet, int totalCols) {
		Row subRow = sheet.createRow(1);
		Cell subCell = subRow.createCell(0);
		subCell.setCellValue("Generado: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, totalCols - 1));

		CellStyle subStyle = wb.createCellStyle();
		Font subFont = wb.createFont();
		subFont.setItalic(true);
		subStyle.setFont(subFont);
		subStyle.setAlignment(HorizontalAlignment.CENTER);
		subStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		subCell.setCellStyle(subStyle);
	}

	private CellStyle crearEstiloHeader(Workbook wb) {
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
		return headerStyle;
	}

	private String val(Object x) {
		return (x == null) ? "-" : String.valueOf(x);
	}

	private String boolSiNo(Boolean b) {
		return (b != null && b) ? "Sí" : "No";
	}

	private String numOrText(Object n) {
		return (n == null) ? "-" : String.valueOf(n);
	}

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
