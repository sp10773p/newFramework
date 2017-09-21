package kr.pe.sdh.core.excel;

import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.HashMap;
import java.util.Map;

/**
 * RGB Color to HEX
 * @author 성동훈
 * @since 2017-01-05
 * @version 1.0
 * @see
 *
 * <pre>
 * == 개정이력(Modification Information) ==
 *
 * 수정일      수정자  수정내용
 * ----------- ------- ---------------------------
 * 2017.01.05  성동훈  최초 생성
 *
 * </pre>
 */
public class RGBColor {
	Map<String, Short> parsedColors = new HashMap<>();

	public short getColor(String color, HSSFWorkbook wb) {
		if(!color.startsWith("#")) color = "#"+color;
		if (!parsedColors.containsKey(color)) {
			int  r=  Integer.valueOf( color.substring( 1, 3 ), 16 );
			int  g=  Integer.valueOf( color.substring( 3, 5 ), 16 );
			int  b=  Integer.valueOf( color.substring( 5, 7 ), 16 );

			HSSFPalette palette = wb.getCustomPalette();
			short col = palette.findSimilarColor(r,g,b).getIndex();
			parsedColors.put(color, col);

			return col;
		} else {
			return parsedColors.get(color);
		}
	}
}