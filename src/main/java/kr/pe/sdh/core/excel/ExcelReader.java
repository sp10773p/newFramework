package kr.pe.sdh.core.excel;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel to Java Object(POI)
 * @author jinhokim
 * @since 2017. 3. 14. 
 * @version 1.0
 *
 * <pre>
 * == 개정이력(Modification Information) ==
 *
 * 수정일      수정자  수정내용
 * ----------- ------- ---------------------------
 * 2017. 3. 14.    jinhokim  최초 생성
 *
 * </pre>
 */
@Service("excelReader")
@SuppressWarnings({"rawtypes", "unchecked"})
public class ExcelReader {
    /**
     * 파일로 액셀객체(Workbook) 객체 생성
     *
     * @param inFile 대상파일
     * @return 액셀객체(Workbook)
     * @throws EncryptedDocumentException
     * @throws InvalidFormatException
     * @throws IOException
     */
    public Workbook getWorkbook(File inFile) throws EncryptedDocumentException, InvalidFormatException, IOException {
        return getWorkbook(new FileInputStream(inFile));
    }

    /**
     * InputStream 으로 액셀객체(Workbook) 객체 생성
     * @param inputStream
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     */
    public Workbook getWorkbook(InputStream inputStream) throws IOException, InvalidFormatException {
        Workbook wb;
        try{
            wb = WorkbookFactory.create(inputStream);
        }finally {
            if(inputStream != null) {
                IOUtils.closeQuietly(inputStream);
            }
        }

        return wb;
    }

    /**
     * Sheet가 하나뿐인 Excel파일의 첫번째 Sheet 객체 생성
     * @param inFile
     * @return 첫번째 Sheet
     * @throws EncryptedDocumentException
     * @throws InvalidFormatException
     * @throws IOException
     */
    public Sheet getFirstSheet(File inFile) throws EncryptedDocumentException, InvalidFormatException, IOException {
        return getWorkbook(inFile).getSheetAt(0);
    }

    /**
     * Sheet가 하나뿐인 Excel파일의 첫번째 Sheet 객체 생성
     * @param inputStream
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     */
    public Sheet getFirstSheet(InputStream inputStream) throws IOException, InvalidFormatException {
        return getWorkbook(inputStream).getSheetAt(0);
    }

    /**
     * 파일로 Row<Cell> 객체를 생성
     * @param inFile
     * @return Row<Cell>
     * @throws EncryptedDocumentException
     * @throws InvalidFormatException
     * @throws IOException
     */
    public Map parseExcelOnlyFirstSheet(File inFile) throws IOException, InvalidFormatException {
        return this.parseExcelSheet(getWorkbook(inFile).getSheetAt(0));
    }

    /**
     * 파일로 Sheet<Row<Cell)>> 객체를 생성
     * @param inFile
     * @return Sheet<Row<Cell>>
     * @throws EncryptedDocumentException
     * @throws InvalidFormatException
     * @throws IOException
     */
    public Map parseExcel(File inFile) throws EncryptedDocumentException, InvalidFormatException, IOException {
        return parseExcel(getWorkbook(inFile));
    }

    /**
     * InputSream으로 Sheet<Row<Cell)>> 객체를 생성
     * @param inputStream
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     */
    public Map parseExcel(InputStream inputStream) throws IOException, InvalidFormatException {
        return parseExcel(getWorkbook(inputStream));
    }

    /**
     * Sheet<Row<Cell)>> 객체를 생성
     * @param wb
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     */
    public Map parseExcel(Workbook wb) throws IOException, InvalidFormatException {
        return parseExcel(wb, 0, 0);
    }

    /**
     * Sheet Row, Column Skip 하고 <Row<Cell)>> 객체를 생성
     * @param wb
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     */
	public Map parseExcel(Workbook wb, int skipRow, int skipCol) throws IOException, InvalidFormatException {
        Map trnMap = new HashMap();

        for(int i = 0; i < wb.getNumberOfSheets(); i++) {
            Sheet sheet = wb.getSheetAt(i);

            trnMap.put(i, this.parseExcelSheet(sheet, skipRow, skipCol));
        }

        return trnMap;
    }

    /**
     * Sheet Row, Column Skip 하고 <Row<Cell)>> 객체를 생성
     * @param wb
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     */
	public Map<Integer, List<List<String>>> parseExcelList(Workbook wb, int skipRow, int skipCol) throws IOException, InvalidFormatException {
        Map trnMap = new HashMap();

        for(int i = 0; i < wb.getNumberOfSheets(); i++) {
            Sheet sheet = wb.getSheetAt(i);
            trnMap.put(i, this.parseExcelListSheet(sheet, skipRow, skipCol));
        }

        return trnMap;
    }

    /**
     * Sheet로 Row<Cell>객체 생성
     * @param sheet
     * @return Row<Cell>
     * @throws EncryptedDocumentException
     * @throws InvalidFormatException
     * @throws IOException
     */
    public Map parseExcelSheet(Sheet sheet) throws EncryptedDocumentException, InvalidFormatException, IOException {
        return parseExcelSheet(sheet, 0);
    }

    /**
     * Sheet로 Row 스킵하고 Row<Cell>객체 생성
     * @param sheet
     * @param skipRow 스킵할 Row 카운트
     * @return Row<Cell>
     * @throws EncryptedDocumentException
     * @throws InvalidFormatException
     * @throws IOException
     */
    public Map parseExcelSheet(Sheet sheet, int skipRow) throws EncryptedDocumentException, InvalidFormatException, IOException {
        return parseExcelSheet(sheet, skipRow, 0);
    }

    /**
     * Sheet로 Row, Column 스킵하고 Row<Cell>객체 생성
     * @param sheet
     * @param skipRow
     * @param skipCol
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     */
    public Map parseExcelSheet(Sheet sheet, int skipRow, int skipCol) throws IOException, InvalidFormatException {
        Map rtnMap = new HashMap();
        if(sheet == null) return rtnMap;

        for(int i = skipRow; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);

            rtnMap.put(i, this.parseExcelRow(row, skipCol));
        }

        return rtnMap;
    }


    /**
     * Sheet로 Row<Cell>객체 생성
     * @param sheet
     * @return Row<Cell>
     * @throws EncryptedDocumentException
     * @throws InvalidFormatException
     * @throws IOException
     */
    public List<List<String>> parseExcelListSheet(Sheet sheet) throws EncryptedDocumentException, InvalidFormatException, IOException {
        return parseExcelListSheet(sheet, 0);
    }


    /**
     * Sheet로 Row 스킵하고 Row<Cell>객체 생성
     * @param sheet
     * @param skipRow 스킵할 Row 카운트
     * @return Row<Cell>
     * @throws EncryptedDocumentException
     * @throws InvalidFormatException
     * @throws IOException
     */
    public List<List<String>> parseExcelListSheet(Sheet sheet, int skipRow) throws EncryptedDocumentException, InvalidFormatException, IOException {
        return parseExcelListSheet(sheet, skipRow, 0);
    }

    /**
     * Sheet로 Row, Column 스킵하고 Row<Cell>객체 생성
     * @param sheet
     * @param skipRow
     * @param skipCol
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     */
    public List<List<String>> parseExcelListSheet(Sheet sheet, int skipRow, int skipCol) throws IOException, InvalidFormatException {
        List<List<String>> list = new ArrayList<>();
        if(sheet == null) return list;

        for(int i = skipRow; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);

            list.add(this.parseExcelListRow(row, skipCol));
        }

        return list;
    }

    /**
     * Row로 <Cell> 객체 생성
     * @param row
     * @return <Cell>
     * @throws EncryptedDocumentException
     * @throws InvalidFormatException
     * @throws IOException
     */
	public Map parseExcelRow(Row row) throws EncryptedDocumentException, InvalidFormatException, IOException {
        return parseExcelRow(row, 0);
    }

    /**
     * Row로 Column 스킵하고 <Cell> 객체 생성
     * @param row
     * @return <Cell>
     * @throws EncryptedDocumentException
     * @throws InvalidFormatException
     * @throws IOException
     */
    public Map parseExcelRow(Row row, int skipCol) throws EncryptedDocumentException, InvalidFormatException, IOException {
        Map rtnMap = new HashMap();
        if(row == null) return rtnMap;

        for(int j = skipCol; j < row.getLastCellNum(); j++) {
            rtnMap.put(j, this.getCellValue(row, j));
        }

        return rtnMap;
    }


    /**
     * Row로 <Cell> 객체 생성
     * @param row
     * @return <Cell>
     * @throws EncryptedDocumentException
     * @throws InvalidFormatException
     * @throws IOException
     */
    public List<String> parseExcelListRow(Row row) throws EncryptedDocumentException, InvalidFormatException, IOException {
        return parseExcelListRow(row, 0);
    }

    /**
     * Row로 Column 스킵하고 <Cell> 객체 생성
     * @param row
     * @return <Cell>
     * @throws EncryptedDocumentException
     * @throws InvalidFormatException
     * @throws IOException
     */
    public List<String> parseExcelListRow(Row row, int skipCol) throws EncryptedDocumentException, InvalidFormatException, IOException {
        List<String> list = new ArrayList<>();
        if(row == null) return list;

        for(int j = skipCol; j < row.getLastCellNum(); j++) {
            list.add(row.getCell(j).getStringCellValue());
        }

        return list;
    }

    /**
     * Row의 Column 값을 반환
     * @param row
     * @param colIdx
     * @return
     */
    public String getCellValue(Row row, int colIdx){
        if(row == null || row.getCell(colIdx) == null){
            return "";
        }
        row.getCell(colIdx).setCellType(Cell.CELL_TYPE_STRING);
        return (row.getCell(colIdx).getStringCellValue() == null ? "" : row.getCell(colIdx).getStringCellValue());
    }

    /**
     * Row의 Column 값을 trim하여 반환
     * @param row
     * @param colIdx
     * @return
     */
    public String getCellTrimValue(Row row, int colIdx){
        return getCellValue(row, colIdx).trim();
    }
}
