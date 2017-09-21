package kr.pe.sdh.core.util;

import org.apache.commons.lang.StringUtils;

/**
 * @author 성동훈
 * @version 1.0
 * @see <pre>
 * == 개정이력(Modification Information) ==
 *
 * 수정일      수정자  수정내용
 * ----------- ------- ---------------------------
 * 2017-04-25  성동훈  최초 생성
 * </pre>
 * @since 2017-04-25
 */
public class XSSUtil {
    public static final int REQUEST_PARAMETER = 1;
    public static final int RESPONSE_PARAMETER = 2;

    public static final int AJAX_DATA = 3;

    public static String cleanReqParamXSS(String value){
        return cleanXSS(REQUEST_PARAMETER, value);
    }


    public static String cleanResParamXSS(String value){
        return cleanXSS(RESPONSE_PARAMETER, value);
    }


    public static String cleanAjaxDataXSS(String value){
        return cleanXSS(AJAX_DATA, value);
    }

    public static String cleanXSS(int type, String value){
        value = value.replaceAll("<", "& lt;").replaceAll(">", "& gt;");
        value = value.replaceAll("eval\\((.*)\\)", "");
        value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
        value = value.replaceAll("script", "");

        if(type != AJAX_DATA){
            value = value.replaceAll("\\(", "& #40;").replaceAll("\\)", "& #41;");
            value = value.replaceAll("'", "& #39;");
        }

        if(type == RESPONSE_PARAMETER){
            value = StringUtils.replace(value, "\r", "");
            value = StringUtils.replace(value, "\n", "");
            value = StringUtils.replace(value, ":", "");
        }

        return value;
    }
}
