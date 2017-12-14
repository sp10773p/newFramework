package kr.pe.sdh.core.base;

import kr.pe.sdh.common.model.UserSessionModel;
import kr.pe.sdh.core.model.AjaxModel;
import kr.pe.sdh.core.util.XSSUtil;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class AjaxGsonMessageConverter extends GsonHttpMessageConverter {
    public AjaxGsonMessageConverter() {
        super();
    }

    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return this.processAjaxModel(type, super.read(type, contextClass, inputMessage));
    }

    /**
     * XSS 처리, 세션 처리
     * @param type
     * @param obj
     */
    private Object processAjaxModel(Type type, Object obj) {
        if(isAjaxModel(type)){
            includeUserSessionModel((AjaxModel)obj);
            processXss((AjaxModel)obj);
        }

        return obj;
    }

    private void processXss(AjaxModel ajaxModel) {
        Map<String, Object> data = ajaxModel.getData();
        List<Map<String, Object>> dataList = ajaxModel.getDataList();

        if(data != null) map(data);
        if(dataList != null) dataList.stream().forEach(mapObject -> map(mapObject));
    }

    private Object xss(Object obj){
        if(obj instanceof Map) return map((Map<String, Object>) obj);
        if(obj instanceof List) return list((List<Object>) obj);
        if(obj instanceof String) return XSSUtil.cleanAjaxDataXSS((String)obj);

        return obj;
    }

    private Map<String, Object> map(Map<String, Object> map){
        map.keySet().stream().forEach(key -> map.put(key, xss(map.get(key))));
        return map;
    }

    private List<Object> list(List<Object> list){
        list.stream().forEach(obj -> obj = xss(obj));
        return list;
    }

    private void includeUserSessionModel(AjaxModel ajaxModel) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        UserSessionModel sessionModel = (UserSessionModel) request.getSession().getAttribute(Constant.SESSION_KEY_USR.getCode());
        if(sessionModel != null){
            ajaxModel.setUserSessionModel(sessionModel);
        }
    }

    private boolean isAjaxModel(Type type) {
        return ((Class) type).getName().equals(AjaxModel.class.getName());
    }

}
