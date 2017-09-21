package kr.pe.sdh.core.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 시스템에서 사용하는 엑셀다운로드를 생성
 * - 엑셀쓰기는 see 를 사용
 * @author 성동훈
 * @version 1.0
 * @see SheetFactory
 * @see CellStyleFactory
 * @see RGBColor
 * <pre>
 * == 개정이력(Modification Information) ==
 *
 * 수정일      수정자  수정내용
 * ----------- ------- ---------------------------
 * 2017-03-07  성동훈  최초 생성
 * </pre>
 * @since 2017-03-07
 */
public class ExcelWriter {

    private HSSFWorkbook wb;

    private String[] headerArr;
    private String[] headerKeyArr;
    private String[] alignArr;

    public ExcelWriter() {
        wb = new HSSFWorkbook();
    }

    public ExcelWriter(HSSFWorkbook wb) {
        this.wb = wb;
    }

    /**
     * 시트 생성
     * @throws IOException
     */
    public void writeSheet(List<Map<String, Object>> headers, List<Map<String, Object>> rowData) throws IOException {
        if(headers == null || rowData == null)
            throw new IllegalArgumentException("타이틀, 데이터 파라미터 정보가 없습니다.");

        SheetFactory factory = new SheetFactory(wb);
        createGridHeader(factory, headers);
        createGridData(factory, rowData);

        factory.autoSizeColumn(1, headerArr.length);
    }

    /**
     * 파일저장
     * @param fileName 저장경로
     * @throws Exception
     */
    public void output(String fileName) throws Exception {
        try(FileOutputStream fos = new FileOutputStream(fileName)){
            wb.write(fos);
        }
    }

    /**
     * Browser Write
     * @param response
     * @throws Exception
     */
    public void output(HttpServletResponse response) throws Exception {
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=excelGrid.xls");
            response.setHeader("Cache-Control", "max-age=0");

            wb.write(response.getOutputStream());
        }catch(Exception e){
            throw e;
        }finally {
            if(wb != null){
                wb.close();
            }
        }
    }

    /**
     * 그리드 데이터 생성
     * @param sheetFactory
     */
    private void createGridData(SheetFactory sheetFactory, List<Map<String, Object>> data) {
        int startRow = 2;
        for (int k = 0; k < data.size(); k++) {
            Map<String, Object> map = data.get(k);
            String[] dataArr = new String[headerKeyArr.length];
            for(int i=0; i<headerKeyArr.length; i++){
                dataArr[i] = (map.get(headerKeyArr[i]) == null ? "" : String.valueOf(map.get(headerKeyArr[i])));
            }

            if (k % 2 != 0) {
                sheetFactory.createStaticRow(startRow + k, 1, dataArr, alignArr, STYLE.GRID_DATA2);
            }else {
                sheetFactory.createStaticRow(startRow + k, 1, dataArr, alignArr, STYLE.GRID_DATA1);
            }
        }
    }

    /**
     * 그리드 헤더 생성
     * @param sheetFactory
     */
    private void createGridHeader(SheetFactory sheetFactory, List<Map<String, Object>> headers) {
        if(headers == null)
            throw new IllegalArgumentException("HEADER 정보가 없습니다.");

        headerArr =  new String[headers.size()];
        headerKeyArr =  new String[headers.size()];
        alignArr =  new String[headers.size()];
        for(int i=0; i<headers.size(); i++){
            Map map = headers.get(i);
            headerArr[i] = (String)map.get("HEAD_TEXT");
            headerKeyArr[i] = (String)map.get("FIELD_NAME");
            alignArr[i] = (map.containsKey("ALIGN") ? (String)map.get("ALIGN") : "center");
        }

        sheetFactory.createStaticRow(1,1, headerArr, STYLE.GRID_HEADER);
    }

}

