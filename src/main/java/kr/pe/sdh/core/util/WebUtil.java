package kr.pe.sdh.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Client 의 IP 정보를 찾는 Util
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
public class WebUtil {
    private static Logger logger = LoggerFactory.getLogger(WebUtil.class);

    public static String getClientIp(HttpServletRequest request){
        String cltAddr = request.getHeader("X-Forwarded-For");

        if(cltAddr == null || cltAddr.equals("")) {
            cltAddr = request.getHeader("Proxy-Client-IP");

            if(cltAddr == null || cltAddr.equals("")) {
                cltAddr = request.getHeader("x-forwarded-ip");
            }
            if(cltAddr == null || cltAddr.equals("")) {
                cltAddr = request.getHeader("REMOTE_ADDR");
            }
            if(cltAddr == null || cltAddr.equals("")) {
                cltAddr = request.getRemoteAddr();
            }
            if(cltAddr == null || cltAddr.equals("")) {
                cltAddr = "";
                logger.info("==========================================\n클라이언트 IP를 구하지 못하였습니다.\n==========================================");
            }
        }

        return cltAddr;
    }
    public static Map<String, ?> getParameterToObject(HttpServletRequest request){
        Map<String, Serializable> params = new HashMap<>();
        Enumeration<?> enumeration = request.getParameterNames();
        String key;
        String[] values;
        while (enumeration.hasMoreElements()) {
            key = (String) enumeration.nextElement();
            values = request.getParameterValues(key);
            if (values != null) {
                for(int i=0; i<values.length; i++){
                    try {
                        values[i] = URLDecoder.decode(values[i], StandardCharsets.UTF_8.toString());
                    } catch (UnsupportedEncodingException e) {
                    }
                }
                params.put(key, (values.length > 1) ? values : values[0]);
            }
        }
        return params;
    }
}
