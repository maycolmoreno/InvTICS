package com.uisrael.consumogestionactivosapi.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
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

import com.uisrael.consumogestionactivosapi.modelo.dto.response.MantenimientoManualResponseDTO;

@Service
public class MantenimientosExcelService {

	private static final String[] COLS = {
			"ID", "EQUIPO", "CÓD. INTERNO", "CUSTODIO", "TÉCNICO",
			"TIPO", "FECHA", "ESTADO GENERAL", "ESTADO INTERNO",
			"PRÓXIMA FECHA", "DETALLE"
	};

	public byte[] generarReporteExcel(List<MantenimientoManualResponseDTO> data, String estado, String tipo) {
		try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			String safeSheetName = WorkbookUtil.createSafeSheetName("Reporte Mantenimientos");
			Sheet sheet = wb.createSheet(safeSheetName);
			int totalCols = COLS.length;

			insertarLogo(wb, sheet);

			// Título
			CellStyle titleStyle = wb.createCellStyle();
			Font titleFont = wb.createFont();
			titleFont.setBold(true);
			titleFont.setFontHeightInPoints((short) 14);
			titleStyle.setFont(titleFont);
			titleStyle.setAlignment(HorizontalAlignment.CENTER);

			Row titleRow = sheet.createRow(1);
			String titulo = "REPORTE DE MANTENIMIENTOS";
			if (estado != null && !estado.isBlank()) {
				titulo += " - Estado: " + estado;
			}
			if (tipo != null && !tipo.isBlank()) {
				titulo += " - Tipo: " + tipo;
			}
			Cell titleCell = titleRow.createCell(0);
			titleCell.setCellValue(titulo);
			titleCell.setCellStyle(titleStyle);
			sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, totalCols - 1));

			// Subtítulo con fecha
			CellStyle subStyle = wb.createCellStyle();
			Font subFont = wb.createFont();
			subFont.setItalic(true);
			subFont.setFontHeightInPoints((short) 10);
			subFont.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
			subStyle.setFont(subFont);
			subStyle.setAlignment(HorizontalAlignment.CENTER);
			Row subRow = sheet.createRow(2);
			Cell subCell = subRow.createCell(0);
			subCell.setCellValue("Generado el " + java.time.LocalDateTime.now()
					.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
			subCell.setCellStyle(subStyle);
			sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, totalCols - 1));

			// Header
			CellStyle headerStyle = wb.createCellStyle();
			Font headerFont = wb.createFont();
			headerFont.setBold(true);
			headerFont.setColor(IndexedColors.WHITE.getIndex());
			headerFont.setFontHeightInPoints((short) 10);
			headerStyle.setFont(headerFont);
			headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
			headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			headerStyle.setAlignment(HorizontalAlignment.CENTER);
			headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			headerStyle.setBorderBottom(BorderStyle.THIN);

			int headerRowNum = 4;
			Row hdr = sheet.createRow(headerRowNum);
			hdr.setHeightInPoints(25);
			for (int i = 0; i < COLS.length; i++) {
				Cell c = hdr.createCell(i);
				c.setCellValue(COLS[i]);
				c.setCellStyle(headerStyle);
			}

			// Body
			CellStyle bodyStyle = wb.createCellStyle();
			bodyStyle.setBorderBottom(BorderStyle.THIN);
			bodyStyle.setBorderTop(BorderStyle.THIN);
			bodyStyle.setBorderLeft(BorderStyle.THIN);
			bodyStyle.setBorderRight(BorderStyle.THIN);
			bodyStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			bodyStyle.setWrapText(true);

			DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			int rowIdx = headerRowNum + 1;
			for (MantenimientoManualResponseDTO m : data) {
				Row row = sheet.createRow(rowIdx++);
				int col = 0;
				setCellValue(row, col++, m.getIdMantenimiento(), bodyStyle);
				setCellValue(row, col++, m.getEquipoDescripcion(), bodyStyle);
				setCellValue(row, col++, m.getEquipoCodigoSap(), bodyStyle);
				setCellValue(row, col++, m.getCustodioNombre(), bodyStyle);
				setCellValue(row, col++, m.getTecnicoNombre(), bodyStyle);
				setCellValue(row, col++, m.getTipoMantenimiento(), bodyStyle);
				setCellValue(row, col++, m.getFechaMantenimiento() != null ? m.getFechaMantenimiento().format(fmt) : "-",
						bodyStyle);
				setCellValue(row, col++, m.getEstadoGeneral(), bodyStyle);
				setCellValue(row, col++, m.getEstadoInterno(), bodyStyle);
				setCellValue(row, col++,
						m.getProximaFecha() != null ? m.getProximaFecha().format(fmt) : "-", bodyStyle);
				setCellValue(row, col++, m.getDetalle(), bodyStyle);
			}

			// Resumen al final
			rowIdx++;
			Row resumenRow = sheet.createRow(rowIdx);
			CellStyle resumenStyle = wb.createCellStyle();
			Font resumenFont = wb.createFont();
			resumenFont.setBold(true);
			resumenStyle.setFont(resumenFont);
			resumenRow.createCell(0).setCellValue("Total de registros:");
			resumenRow.getCell(0).setCellStyle(resumenStyle);
			resumenRow.createCell(1).setCellValue(data.size());

			// Autofit
			for (int i = 0; i < totalCols; i++) {
				sheet.autoSizeColumn(i);
				int w = sheet.getColumnWidth(i);
				if (w > 10000) {
					sheet.setColumnWidth(i, 10000);
				}
			}

			sheet.createFreezePane(0, headerRowNum + 1);
			sheet.setAutoFilter(new CellRangeAddress(headerRowNum, headerRowNum, 0, totalCols - 1));

			wb.write(out);
			return out.toByteArray();

		} catch (Exception e) {
			throw new RuntimeException("Error al generar Excel de mantenimientos", e);
		}
	}

	private void setCellValue(Row row, int col, Object value, CellStyle style) {
		Cell cell = row.createCell(col);
		if (value instanceof Number n) {
			cell.setCellValue(n.doubleValue());
		} else {
			cell.setCellValue(value != null ? value.toString() : "-");
		}
		cell.setCellStyle(style);
	}

	private void insertarLogo(Workbook wb, Sheet sheet) {
		try {
			InputStream logo = getClass().getResourceAsStream("/static/assets/images/cresio-logo.png");
			if (logo == null) {
				logo = getClass().getResourceAsStream("/static/assets/images/logo.png");
			}
			if (logo != null) {
				byte[] bytes = logo.readAllBytes();
				logo.close();
				int picIdx = wb.addPicture(bytes,
						Workbook.PICTURE_TYPE_PNG);
				CreationHelper helper = wb.getCreationHelper();
				Drawing<?> drawing = sheet.createDrawingPatriarch();
				ClientAnchor anchor = helper.createClientAnchor();
				anchor.setCol1(0);
				anchor.setRow1(0);
				anchor.setCol2(2);
				anchor.setRow2(1);
				drawing.createPicture(anchor, picIdx);
			}
		} catch (Exception ignored) {
			// Logo opcional
		}
	}
}
