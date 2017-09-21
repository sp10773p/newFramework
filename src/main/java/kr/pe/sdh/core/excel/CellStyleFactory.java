package kr.pe.sdh.core.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;

import java.util.HashMap;
import java.util.Map;

/**
 * 엑셀 셀 스타일을 생성
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
public class CellStyleFactory {

    private short defaultGridFontSize = 10;
    private String defaultFontName = "Arial";

    private RGBColor rgbColor = new RGBColor();
    private HSSFWorkbook wb;
    private Map<STYLE, Font> fontMap = new HashMap<>();
    private Map<String, CellStyle> cellStyleMap = new HashMap<>();

    public CellStyleFactory(HSSFWorkbook wb){
        this.wb = wb;
    }

    /**
     * 지정돈 스타일의 폰트를 바환
     * @param EXCELStyle
     * @return
     */
    public Font getStaticFont(STYLE EXCELStyle){
        if(!fontMap.containsKey(EXCELStyle)){
            switch (EXCELStyle){
                case GRID_HEADER:
                    fontMap.put(EXCELStyle, getFont(defaultGridFontSize, Font.BOLDWEIGHT_BOLD, HSSFColor.WHITE.index));
                    break;

                case GRID_DATA1:
                case GRID_DATA2:
                    fontMap.put(EXCELStyle, getFont(defaultGridFontSize, Font.BOLDWEIGHT_NORMAL, HSSFColor.BLACK.index));
                    break;

                case TITLE:
                    fontMap.put(EXCELStyle, getFont((short)14, Font.BOLDWEIGHT_BOLD, HSSFColor.BLACK.index));
                    break;

                case SUB_TITLE:
                case HEADER_TITLE:
                    fontMap.put(EXCELStyle, getFont((short)11, Font.BOLDWEIGHT_BOLD, HSSFColor.BLACK.index));
                    break;

                case DATA1:
                    fontMap.put(EXCELStyle, getFont((short)9));
                    break;
            }
        }

        return fontMap.get(EXCELStyle);
    }

    /**
     * 파라미터 사이즈의 폰트를 반환
     * @param fontSize
     * @return
     */
    public Font getFont(short fontSize){
        return getFont(defaultFontName, fontSize, Font.BOLDWEIGHT_NORMAL, HSSFColor.BLACK.index);
    }

    /**
     * 파라미터 사이즈, 굵기, 색상의 폰트를 반환
     * @param fontSize
     * @param boldWeight
     * @param color
     * @return
     */
    public Font getFont(short fontSize, short boldWeight, short color){
        return getFont(defaultFontName, fontSize, boldWeight, color);
    }

    /**
     * 파라미터 폰트명, 사이즈, 굵기, 색상의 폰트를 반환
     * @param fontName
     * @param fontSize
     * @param boldWeight
     * @param color
     * @return
     */
    public Font getFont(String fontName, short fontSize, short boldWeight, short color){
        Font font = wb.createFont();

        font.setFontHeightInPoints(fontSize);
        font.setBoldweight(boldWeight);
        font.setColor(color);
        font.setFontName(fontName);

        return font;
    }

    /**
     * 지정된 셀 스타일을 반환
     * @param style
     * @return
     */
    public CellStyle getStaticCellStyle(STYLE style){
        return getStaticCellStyle(style, CellStyle.ALIGN_CENTER);
    }

    /**
     * 지정된 셀 스타일을 반환
     * @param style
     * @param align
     * @return
     */
    public CellStyle getStaticCellStyle(STYLE style, short align){
        String styleKey = style.toString()+align;
        if(!cellStyleMap.containsKey(styleKey)){

            Font font = getStaticFont(style);
            switch (style){
                case GRID_HEADER:
                    cellStyleMap.put(styleKey, getCellStyle(font, "#A2A6AA", "#808080"));
                    break;

                case DATA1:
                case GRID_DATA1:
                    cellStyleMap.put(styleKey, getCellStyle(font, align));
                    break;

                case GRID_DATA2:
                    cellStyleMap.put(styleKey, getCellStyle(font, align, "#BFFCFE"));
                    break;

                case TITLE:
                    cellStyleMap.put(styleKey, getCellStyle(font, align, "#538DD5"));
                    break;

                case SUB_TITLE:
                    cellStyleMap.put(styleKey, getCellStyle(font, align, "#BFBFBF"));
                    break;

                case HEADER_TITLE:
                    cellStyleMap.put(styleKey, getCellStyle(font, align, "#FFFF00"));
                    break;
            }
        }

        return cellStyleMap.get(styleKey);
    }

    /**
     * 파라미터의 폰트를 가지는 셀 스타일을 반환
     * @param font
     * @return
     */
    public CellStyle getCellStyle(Font font){
        return getCellStyle(font, "#FFFFFF", "#000000", CellStyle.ALIGN_CENTER, CellStyle.VERTICAL_CENTER, false);
    }

    /**
     * 파라미터의 폰트를 가지는 셀 스타일을 반환
     * @param font
     * @return
     */
    public CellStyle getCellStyle(Font font, short align){
        return getCellStyle(font, "#FFFFFF", "#000000", align, CellStyle.VERTICAL_CENTER, false);
    }

    /**
     * 파라미터의 폰트를 가지는 셀 스타일을 반환
     * @param font
     * @return
     */
    public CellStyle getCellStyle(Font font, short align, String bgColor){
        return getCellStyle(font, bgColor, "#000000", align, CellStyle.VERTICAL_CENTER, false);
    }

    /**
     * 파라미터의 폰트를 가지는 셀 스타일을 반환
     * @param font
     * @return
     */
    public CellStyle getCellStyle(Font font, String bgColor, String borderColor){
        return getCellStyle(font, bgColor, borderColor, CellStyle.ALIGN_CENTER, CellStyle.VERTICAL_CENTER, false);
    }

    /**
     * 파라미터의 폰트를 가지는 셀 스타일을 반환
     * @param font
     * @return
     */
    public CellStyle getCellStyle(Font font, String bgColor, String borderColor, short align){
        return getCellStyle(font, bgColor, borderColor, align, CellStyle.VERTICAL_CENTER, false);
    }

    /**
     * 파라미터의 폰트를 가지는 셀 스타일을 반환
     * @param font
     * @return
     */
    public CellStyle getCellStyle(Font font, String bgColor, String borderColor, short align, short vAlign, boolean wrapText){
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setFont(font);
        cellStyle.setAlignment(align);

        cellStyle.setBottomBorderColor(rgbColor.getColor(borderColor, wb));
        cellStyle.setTopBorderColor(rgbColor.getColor(borderColor, wb));
        cellStyle.setLeftBorderColor(rgbColor.getColor(borderColor, wb));
        cellStyle.setRightBorderColor(rgbColor.getColor(borderColor, wb));

        cellStyle.setBorderTop(CellStyle.BORDER_THIN);
        cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
        cellStyle.setBorderRight(CellStyle.BORDER_THIN);

        if(bgColor != null && !bgColor.isEmpty()){
            cellStyle.setFillForegroundColor(rgbColor.getColor(bgColor, wb));
            cellStyle.setFillPattern(cellStyle.SOLID_FOREGROUND);
        }

        cellStyle.setVerticalAlignment(vAlign);
        cellStyle.setWrapText(wrapText);

        return cellStyle;
    }
}
