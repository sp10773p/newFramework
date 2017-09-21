package kr.pe.sdh.core.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * 엑셀 시트를 생성하는 클래스
 * @author 성동훈
 * @version 1.0
 * @see <pre>
 * == 개정이력(Modification Information) ==
 *
 * 수정일      수정자  수정내용
 * ----------- ------- ---------------------------
 * 2017-03-13  성동훈  최초 생성
 * </pre>
 * @since 2017-03-13
 */
public class SheetFactory {
    HSSFWorkbook wb;
    Sheet sheet;

    CellStyleFactory cellStyleFactory;
    public SheetFactory(HSSFWorkbook wb){
        this.wb = wb;
        this.cellStyleFactory = new CellStyleFactory(this.wb);
        this.sheet = this.wb.createSheet();
        //this.sheet.setDefaultColumnWidth(2800);
    }

    public SheetFactory(HSSFWorkbook wb, String sheetName){
        this.wb = wb;
        this.cellStyleFactory = new CellStyleFactory(this.wb);
        this.sheet = wb.createSheet(sheetName);
    }

    /**
     * 시스템에서 지정된 스타일로 로우를 생성
     * @param startRow : 시작 로우 Index
     * @param startCol : 시작 컬럼 Index
     * @param cellData : Cell Value Array
     * @param style : 지정된 스타일
     */
    public void createStaticRow(int startRow, int startCol, String[] cellData, STYLE style){
        createRow(startRow, startCol, cellData, this.cellStyleFactory.getStaticCellStyle(style));
    }

    /**
     * 시스템에서 지정된 스타일로 로우를 생성
     * @param startRow : 시작 로우 Index
     * @param startCol : 시작 컬럼 Index
     * @param cellData : Cell Value Array
     * @param alignData : Cell Align Array
     * @param style : 지정된 스타일
     */
    public void createStaticRow(int startRow, int startCol, String[] cellData, String[] alignData, STYLE style){
        Row row = sheet.createRow(startRow);
        for(int i=0; i<cellData.length; i++){
            short align = CellStyle.ALIGN_CENTER;
            if(alignData[i] != null){
                switch (alignData[i]){
                    case "left":
                        align = CellStyle.ALIGN_LEFT;
                        break;

                    case "right":
                        align = CellStyle.ALIGN_RIGHT;
                        break;

                    default:
                        align = CellStyle.ALIGN_CENTER;
                        break;
                }
            }

            CellStyle cellStyle = cellStyleFactory.getStaticCellStyle(style, align);
            createCell(row, startCol+i, cellData[i], cellStyle);
        }
    }

    /**
     * 지정된 로우를 생성
     * @param startRow : 생성할 로우 Index
     * @return
     */
    public Row createRow(int startRow){
        return sheet.createRow(startRow);
    }

    /**
     * 지정된 로우를 생성
     * @param startRow : 생성할 로우 Index
     * @param startCol : 생성할 로우의 시작 컬럼 Index
     * @param cellData : Cell Value Array
     * @param cellStyle : 지정된 스타일
     */
    public void createRow(int startRow, int startCol, String[] cellData, CellStyle cellStyle){
        Row row = sheet.createRow(startRow);
        for(int i=0; i<cellData.length; i++){
            createCell(row, startCol+i, cellData[i], cellStyle);
        }
    }

    /**
     * 셀을 생성
     * @param row
     * @param startCol
     * @param value
     * @param cellStyle
     */
    public void createCell(Row row, int startCol, String value, CellStyle cellStyle){
        Cell cell = row.createCell(startCol);
        cell.setCellStyle(cellStyle);
        cell.setCellValue(value);
    }

    /**
     * 병합 셀을 생성
     * @param value
     * @param cellStyle
     * @param firstRow
     * @param firstCol
     * @param lastRow
     * @param lastCol
     */
    public void createMergeCell(String value, CellStyle cellStyle, int firstRow, int firstCol, int lastRow, int lastCol){
        for(int i=firstRow; i<=lastRow; i++){
            Row row;
            if(sheet.getRow(i) == null){
                row = sheet.createRow(i);
            }else{
                row = sheet.getRow(i);
            }

            for(int k=firstCol; k<=lastCol; k++){
                Cell cell = row.createCell(k);
                cell.setCellStyle(cellStyle);

                if(k == firstCol)
                    cell.setCellValue(value);
            }
        }

        merge(firstRow, firstCol, lastRow, lastCol);
    }

    /**
     * 데이터 셀을 생성
     * @param data
     */
    public void createData(String[] data) {
        String[] align = new String[data.length];
        for(int i=0; i<data.length; i++){
            align[i] = "center";
        }
        createData(data, align, 0, 0);
    }

    /**
     * 데이터 셀을 생성
     * @param data
     * @param align
     * @param startRow
     * @param startCol
     */
    public void createData(String[] data, String[] align, int startRow, int startCol) {
        for (int k = 0; k < data.length; k++) {
            createStaticRow(startRow, startCol, data, align, STYLE.GRID_DATA1);
        }
    }

    /**
     * 셀을 병합
     * @param firstRow
     * @param firstCol
     * @param lastRow
     * @param lastCol
     */
    public void merge(int firstRow, int firstCol, int lastRow, int lastCol){
        sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
    }

    /**
     * 자동 넓이 조절
     * @param startCol
     * @param colSize
     */
    public void autoSizeColumn(int startCol, int colSize){
        for(int i=startCol; i<(startCol + colSize); i++){
            this.sheet.autoSizeColumn(i);
        }
    }

    /**
     * 넓이 조정
     * @param startCol
     * @param width
     */
    public void setSizeColumn(int startCol, int[] width) {
        for(int i=0; i<width.length; i++){
            int size = width[i] * 20;
            this.sheet.setColumnWidth(startCol+i, size);
        }
    }

    public Sheet getSheet() {
        return sheet;
    }

    public HSSFWorkbook getWorkBook() {
        return wb;
    }

}
