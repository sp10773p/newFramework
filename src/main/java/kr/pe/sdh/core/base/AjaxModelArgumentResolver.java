package kr.pe.sdh.core.base;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import kr.pe.sdh.core.model.AccessLogModel;
import kr.pe.sdh.core.model.AjaxModel;
import kr.pe.sdh.core.model.UserSessionModel;
import kr.pe.sdh.core.service.CommonService;
import kr.pe.sdh.core.util.KeyGen;
import kr.pe.sdh.core.util.WebUtil;
import kr.pe.sdh.core.util.XSSUtil;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Client에서 넘기는 객체를 AjaxModel로 변환
 * AccelLog 처리
 * UsrSession 정보를 'SS'를 접두어로 파라미터 추가
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
public class AjaxModelArgumentResolver implements HandlerMethodArgumentResolver {
    @Resource(name = "commonService")
    private CommonService commonService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return AjaxModel.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        AjaxModel model = new AjaxModel();

        Map<String, Object> reqMap = new HashMap<>();
        String body = this.getBody(request);

        if(body != null){
            body = XSSUtil.cleanAjaxDataXSS(URLDecoder.decode(body, StandardCharsets.UTF_8.toString()));

            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new StringReader(body));
            reader.setLenient(true);
            reqMap = gson.fromJson(reader, Map.class);
        }

        Map<String, Object> data = (Map<String, Object>) reqMap.get("data");

        if (data == null) {
            data = new HashMap<>();
        }

        data.putAll(WebUtil.getParameterToObject(request));

        model.setData(data);
        model.setDataList((List) reqMap.get("dataList"));
        model.setStatus(Integer.parseInt(String.valueOf(reqMap.get("status"))));
        model.setMsg((String) reqMap.get("msg"));

        String sid = KeyGen.getRandomTimeKey();
        String actionMenuId = (String) data.get(Constant.ACTION_MENU_ID.getCode());
        String actionMenuNm = (String) data.get(Constant.ACTION_MENU_NM.getCode());
        String actionMenuDiv = (String) data.get(Constant.ACTION_MENU_DIV.getCode());
        String actionNm = (String) data.get(Constant.ACTION_NM.getCode());

        HttpSession session = request.getSession();
        UserSessionModel sessionModel = commonService.getUesrSessionModel(request);

        String sessionId = session.getId();

        if (sessionModel != null) {
            model.setUserSessionModel(sessionModel);

            addUsrIinfoToModel(model);
            try {
                if (!StringUtils.isEmpty(actionMenuId)) {
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.putAll(data);

                    Map<String, Object> paramMap = new HashMap<>();
                    for (Map.Entry<String, Object> entry : data.entrySet()) {
                        if (entry.getKey().startsWith("SS")) {
                            continue;
                        }
                        paramMap.put(entry.getKey(), entry.getValue());
                    }

                    // Access Log Write
                    AccessLogModel accessLogModel = new AccessLogModel();

                    accessLogModel.setSid(sid);
                    accessLogModel.setSessionId(sessionId);
                    accessLogModel.setLoginIp(sessionModel.getUserIp());
                    accessLogModel.setUserId(sessionModel.getUserId());
                    accessLogModel.setScreenId(actionMenuId);
                    accessLogModel.setScreenNm(actionMenuNm);
                    accessLogModel.setLogDiv(actionMenuDiv);
                    accessLogModel.setParam(new Gson().toJson(paramMap));
                    accessLogModel.setUri(request.getRequestURI());
                    accessLogModel.setRmk(actionNm);

                    commonService.saveAccessLog(accessLogModel);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return model;
    }

    private void addUsrIinfoToModel(AjaxModel model) throws Exception {
        commonService.addUsrIinfoToMap(model.getUserSessionModel(), model.getData());
    }

    private String getBody(HttpServletRequest request) throws IOException {
        String body = null;
        try(BufferedInputStream in = new BufferedInputStream(request.getInputStream())){
            try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                byte[] abytes = new byte[1024 * 8];
                int readInd;
                while ((readInd = in.read(abytes, 0, abytes.length)) > 0) {
                    out.write(abytes, 0, readInd);
                }

                body = new String(out.toByteArray(), StandardCharsets.UTF_8);
                body = body.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
                body = body.replaceAll("\\+", "%2B");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return body;
    }
}
