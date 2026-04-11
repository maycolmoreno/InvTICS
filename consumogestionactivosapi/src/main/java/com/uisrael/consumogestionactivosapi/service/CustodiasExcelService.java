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

import com.uisrael.consumogestionactivosapi.modelo.dto.response.CustodiasResponseDTO;

@Service
public class CustodiasExcelService {

	private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	private static final String[] COLUMNAS = {
			"CUSTODIO", "C\u00c9DULA", "FECHA INICIO", "FECHA FIN",
			"C\u00d3DIGO SAP", "TIPO", "MODELO", "SERIAL", "OBSERVACI\u00d3N"
	};

	public byte[] generarReporteExcel(List<CustodiasResponseDTO> data,
			Integer custodioId, Integer equipoId) {

		try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			Sheet sheet = wb.createSheet(WorkbookUtil.createSafeSheetName("Reporte Custodias"));
			int totalCols = COLUMNAS.length;

			insertarLogo(wb, sheet);
			crearTitulo(wb, sheet, totalCols, custodioId, equipoId);
			crearSubtitulo(wb, sheet, totalCols);

			CellStyle headerStyle = crearEstiloHeader(wb);
			int headerRowIndex = 2;
			Row header = sheet.createRow(headerRowIndex);
			header.setHeightInPoints(18);
			for (int i = 0; i < COLUMNAS.length; i++) {
				Cell c = header.createCell(i);
				c.setCellValue(COLUMNAS[i]);
				c.setCellStyle(headerStyle);
			}

			CellStyle dataStyle = wb.createCellStyle();
			dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);

			int r = headerRowIndex + 1;
			for (CustodiasResponseDTO x : data) {
				Row row = sheet.createRow(r++);
				int c = 0;

				row.createCell(c++).setCellValue(custodioVal(x, "nombre"));
				row.createCell(c++).setCellValue(custodioVal(x, "cedula"));
				row.createCell(c++).setCellValue(formatFecha(x.getFechaInicio()));
				row.createCell(c++).setCellValue(formatFecha(x.getFechaFin()));
				row.createCell(c++).setCellValue(equipoVal(x, "codigoSap"));
				row.createCell(c++).setCellValue(equipoVal(x, "tipo"));
				row.createCell(c++).setCellValue(equipoVal(x, "modelo"));
				row.createCell(c++).setCellValue(equipoVal(x, "serial"));
				row.createCell(c++).setCellValue(val(x.getObservacion()));

				for (int i = 0; i < COLUMNAS.length; i++) {
					Cell cell = row.getCell(i);
					if (cell != null) cell.setCellStyle(dataStyle);
				}
			}

			sheet.createFreezePane(0, headerRowIndex + 1);
			sheet.setAutoFilter(new CellRangeAddress(headerRowIndex, headerRowIndex, 0, totalCols - 1));
			for (int i = 0; i < COLUMNAS.length; i++) sheet.autoSizeColumn(i);

			wb.write(out);
			return out.toByteArray();

		} catch (Exception ex) {
			throw new RuntimeException("Error generando Excel de custodias", ex);
		}
	}

	// === Helpers privados ===

	private void insertarLogo(Workbook wb, Sheet sheet) {
		try (InputStream is = getClass().getClassLoader()
				.getResourceAsStream("static/assets/images/Logo-AMC-Oficial.png")) {
			if (is == null) return;

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
		} catch (Exception e) {
			// Logo no disponible, no bloquear el reporte
		}
	}

	private void crearTitulo(Workbook wb, Sheet sheet, int totalCols,
			Integer custodioId, Integer equipoId) {
		String titulo = "REPORTE DE CUSTODIAS"
				+ ((custodioId != null && custodioId > 0) ? " - Custodio: " + custodioId : "")
				+ ((equipoId != null && equipoId > 0) ? " - Equipo: " + equipoId : "");

		Row titleRow = sheet.createRow(0);
		titleRow.setHeightInPoints(28);
		Cell titleCell = titleRow.createCell(0);
		titleCell.setCellValue(titulo);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, totalCols - 1));

		CellStyle style = wb.createCellStyle();
		Font font = wb.createFont();
		font.setBold(true);
		font.setFontHeightInPoints((short) 14);
		style.setFont(font);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		titleCell.setCellStyle(style);
	}

	private void crearSubtitulo(Workbook wb, Sheet sheet, int totalCols) {
		Row subRow = sheet.createRow(1);
		Cell subCell = subRow.createCell(0);
		subCell.setCellValue("Generado: " + LocalDateTime.now()
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, totalCols - 1));

		CellStyle style = wb.createCellStyle();
		Font font = wb.createFont();
		font.setItalic(true);
		style.setFont(font);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		subCell.setCellStyle(style);
	}

	private CellStyle crearEstiloHeader(Workbook wb) {
		CellStyle style = wb.createCellStyle();
		Font font = wb.createFont();
		font.setBold(true);
		font.setColor(IndexedColors.WHITE.getIndex());
		style.setFont(font);
		style.setFillForegroundColor(IndexedColors.GREEN.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		return style;
	}

	private String custodioVal(CustodiasResponseDTO x, String campo) {
		if (x.getFkCustodio() == null) return "-";
		return switch (campo) {
			case "nombre" -> val(x.getFkCustodio().getNombre());
			case "cedula" -> val(x.getFkCustodio().getCedula());
			default -> "-";
		};
	}

	private String equipoVal(CustodiasResponseDTO x, String campo) {
		if (x.getFkEquipo() == null) return "-";
		return switch (campo) {
			case "codigoSap" -> val(x.getFkEquipo().getCodigoSap());
			case "tipo" -> val(x.getFkEquipo().getFkCategoria() != null ? x.getFkEquipo().getFkCategoria().getNombre() : null);
			case "modelo" -> val(x.getFkEquipo().getModelo());
			case "serial" -> val(x.getFkEquipo().getSerial());
			default -> "-";
		};
	}

	private String val(Object x) {
		return x == null ? "-" : String.valueOf(x);
	}

	private String formatFecha(LocalDate fecha) {
		return fecha == null ? "-" : fecha.format(FMT);
	}
}
