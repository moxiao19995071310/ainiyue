package com.loveyou.webController.common;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * 导入导出Excel工具类
 * 
 * @author Hu Xiaobo
 * 
 */
public class ExcelUtil {

	/**
	 * 导入excel工具类
	 * 
	 * @param list
	 * @param keys
	 * @param columnNames
	 */
	public static Workbook createWorkBook(List<Map<String, Object>> list, String[] keys, String columnNames[]) {
		Workbook wb = new HSSFWorkbook();
		Sheet sheet = wb.createSheet(list.get(0).get("sheetName").toString());
		for (int i = 0; i < keys.length; i++) {
			sheet.setColumnWidth((short) i, (short) (35.7 * 120));
		}

		Row row = sheet.createRow((short) 0);

		CellStyle cs = wb.createCellStyle();
		CellStyle cs2 = wb.createCellStyle();

		Font f = wb.createFont();
		Font f2 = wb.createFont();

		f.setFontHeightInPoints((short) 10);
		f.setColor(IndexedColors.BLACK.getIndex());
		f.setBoldweight(Font.BOLDWEIGHT_BOLD);

		f2.setFontHeightInPoints((short) 10);
		f2.setColor(IndexedColors.BLACK.getIndex());

		// Font f3=wb.createFont();
		// f3.setFontHeightInPoints((short) 10);
		// f3.setColor(IndexedColors.RED.getIndex());

		cs.setFont(f);
		cs.setBorderLeft(CellStyle.BORDER_THIN);
		cs.setBorderRight(CellStyle.BORDER_THIN);
		cs.setBorderTop(CellStyle.BORDER_THIN);
		cs.setBorderBottom(CellStyle.BORDER_THIN);
		cs.setAlignment(CellStyle.ALIGN_CENTER);

		cs2.setFont(f2);
		cs2.setBorderLeft(CellStyle.BORDER_THIN);
		cs2.setBorderRight(CellStyle.BORDER_THIN);
		cs2.setBorderTop(CellStyle.BORDER_THIN);
		cs2.setBorderBottom(CellStyle.BORDER_THIN);
		cs2.setAlignment(CellStyle.ALIGN_CENTER);
		for (int i = 0; i < columnNames.length; i++) {
			Cell cell = row.createCell(i);
			cell.setCellValue(columnNames[i]);
			cell.setCellStyle(cs);
		}
		for (short i = 1; i < list.size(); i++) {
			Row row1 = sheet.createRow((short) i);
			for (short j = 0; j < keys.length; j++) {
				Cell cell = row1.createCell(j);
				cell.setCellValue(list.get(i).get(keys[j]) == null ? " " : list.get(i).get(keys[j]).toString());
				cell.setCellStyle(cs2);
			}
		}
		return wb;
	}


	private static List readExcel(InputStream in) throws IOException {
		HSSFWorkbook hssfWorkbook = new HSSFWorkbook(in);
		// 循环工作表Sheet
		for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
			HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			List<String> bqls = new ArrayList<String>();
			if (hssfSheet == null) {
				continue;
			}
			// 循环行Row
			for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
				HSSFRow hssfRow = hssfSheet.getRow(rowNum);
				for (int i = 0; i < hssfRow.getLastCellNum(); i++) {
					HSSFCell brandIdHSSFCell = hssfRow.getCell(i);
					if (i == 0) {
						paramMap.put("goods_commonid", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 1) {
						paramMap.put("goods_name", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 2) {
						paramMap.put("goods_jingle", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 3) {
						paramMap.put("gc_id", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 4) {
						paramMap.put("gc_name", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 5) {
						paramMap.put("store_id", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 6) {
						paramMap.put("store_name", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 7) {
						paramMap.put("spec_name", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 8) {
						paramMap.put("spec_value", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 9) {
						paramMap.put("brand_id", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 10) {
						paramMap.put("brand_name", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 11) {
						paramMap.put("goods_attr", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 11) {
						paramMap.put("goods_body", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 11) {
						paramMap.put("goods_state", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 11) {
						paramMap.put("goods_stateremark", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 11) {
						paramMap.put("goods_verify", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 11) {
						paramMap.put("goods_verifyremark", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 11) {
						paramMap.put("goods_lock", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 11) {
						paramMap.put("goods_addtime", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 11) {
						paramMap.put("goods_selltime", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 11) {
						paramMap.put("goods_specname", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 11) {
						paramMap.put("goods_price", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 11) {
						paramMap.put("goods_marketprice", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 11) {
						paramMap.put("goods_costprice", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 11) {
						paramMap.put("goods_discount", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 11) {
						paramMap.put("goods_serial", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 11) {
						paramMap.put("transport_id", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 11) {
						paramMap.put("transport_title", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 11) {
						paramMap.put("goods_commend", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 11) {
						paramMap.put("goods_freight", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 11) {
						paramMap.put("goods_vat", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 11) {
						paramMap.put("areaid_1", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 11) {
						paramMap.put("areaid_2", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 11) {
						paramMap.put("goods_stcids", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 11) {
						paramMap.put("plateid_top", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 11) {
						paramMap.put("plateid_bottom", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 11) {
						paramMap.put("goods_id", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 11) {
						paramMap.put("goods_serial", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 11) {
						paramMap.put("goods_click", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 11) {
						paramMap.put("goods_salenum", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 11) {
						paramMap.put("goods_collect", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 11) {
						paramMap.put("goods_spec", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 11) {
						paramMap.put("goods_storage", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 11) {
						paramMap.put("goods_state", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 11) {
						paramMap.put("goods_verify", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 11) {
						paramMap.put("color_id", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 11) {
						paramMap.put("goods_collect", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 11) {
						paramMap.put("evaluation_good_star", brandIdHSSFCell.getNumericCellValue());
					} else if (i == 11) {
						paramMap.put("evaluation_count", brandIdHSSFCell.getNumericCellValue());
					}
					String sql1 = "insert into loveyou_goods_common "
							+ "(goods_commonid,goods_name,goods_jingle,gc_id,gc_name,"
							+ "store_id,store_name,spec_name,spec_value,brand_id,brand_name,"
							+ "type_id,goods_attr,goods_body,goods_state,goods_stateremark,"
							+ "goods_verify,goods_verifyremark,goods_lock,goods_addtime,"
							+ "goods_selltime,goods_specname,goods_price,goods_marketprice,"
							+ "goods_costprice,goods_discount,goods_serial,transport_id,"
							+ "transport_title,goods_commend,goods_freight,goods_vat,"
							+ "areaid_1,areaid_2,goods_stcids,plateid_top,plateid_bottom) " + "values ("
							+ paramMap.get("goods_commonid") + ",)";
				}
			}
		}
		return null;
	}

}