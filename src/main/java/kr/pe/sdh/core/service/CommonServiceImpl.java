package kr.pe.sdh.core.service;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import kr.pe.sdh.core.base.Constant;
import kr.pe.sdh.core.dao.AbstractDAO;
import kr.pe.sdh.core.dao.CommonDAOFactory;
import kr.pe.sdh.core.excel.ExcelWriter;
import kr.pe.sdh.core.model.AccessLogModel;
import kr.pe.sdh.core.model.AjaxModel;
import kr.pe.sdh.core.model.LogMngModel;
import kr.pe.sdh.core.model.UserSessionModel;
import kr.pe.sdh.core.util.DateUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.mybatis.spring.MyBatisSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.*;

/**
 * 공통 서비스 처리 추상 클래스를 구현
 * @author 성동훈
 * @since 2017-01-03
 * @version 1.0
 * @see CommonService
 *
 * <pre>
 * == 개정이력(Modification Information) ==
 *
 * 수정일      수정자  수정내용
 * ----------- ------- ---------------------------
 * 2017.01.03  성동훈  최초 생성
 *
 * </pre>
 */
@Service(value = "commonService")
public class CommonServiceImpl implements CommonService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource(name = "commonDAOFactory")
    private CommonDAOFactory daoFactory;

    //@Resource(name="mailSender") // TODO 메일 설정시 주석 풀것
    private JavaMailSenderImpl mailSender;

    @Value("#{config['site.name']}")
    private String siteName;

    @Value("#{config['summary.from.date.period']}")
    private String datePeriod;

    /* 로그 필터 관리 */
    private List<LogMngModel> logMngList;

    /* 알림메시지 캐시 */
    private Map<String, String> messageStorage;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Map<String, String>> getMessageAll() {
        return daoFactory.getDao().list("common.getMessageList");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized Map<String, Object> getMessageData() {
        Map<String, Object> message = new HashMap<>();
        if(messageStorage == null){
            loadMessage(message);

        }else{
            message.putAll(messageStorage);

        }

        return message;
    }

    @Override
    public synchronized void loadMessage(Map<String, Object> message) {
        if(logger.isDebugEnabled()){
            logger.debug("Load Message Storage Cache!!!");
        }

        messageStorage = new HashMap<>();
        List<Map<String, String>> messageList = getMessageAll();
        for(Map<String, String> map : messageList){
            String key = map.get("TYPE")+map.get("CODE");
            String val = map.get("MESSAGE");

            if(message != null) {
                message.put(key, val);
            }
            messageStorage.put(key, val);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage(String code) {
        return (String)getMessageData().get(code);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage(String code, String arg) {
        return getMessage(code).replace("$", arg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage(String code, List<String> args) {
        String msg = getMessage(code);
        for(int i=0; i<args.size(); i++){
            msg = msg.replace("$"+(i+1), args.get(i));
        }

        return msg;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage(String code, String[] args) {
        return getMessage(code, Arrays.asList(args));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AjaxModel selectGridPagingList(AjaxModel model) {
        Map<String, Object> param = model.getData();

        // 필수 쿼리 아이디 체크
        if(StringUtils.isEmpty((String)param.get(Constant.QUERY_KEY.getCode()))){
            model.setStatus(-1);
            model.setMsg(getMessage("E00000001")); // 조회 쿼리 ID가 존재하지 않습니다.

            return model;
        }

        // 조회쿼리
        String qKey = (String)param.get(Constant.QUERY_KEY.getCode());

        //카운트 쿼리
        String countQKey = qKey + Constant.SUFFIX_COUNT_STR.getCode();

        //합계/집계 쿼리
        String summaryQKey = (String)param.get(Constant.SUMMARY_QUERY_KEY.getCode());

        AbstractDAO dao = daoFactory.getDao(getDbPoolName(param));

        boolean isExistCount = true;

        int totalCount = 0;
        try{
            totalCount = Integer.parseInt(String.valueOf(dao.select(countQKey, param)));
        }catch (MyBatisSystemException e){
            isExistCount = false;
        }

        List<Map<String, Object>> result;

        if(param.get("PAGE_INDEX") != null && param.get("PAGE_ROW") != null){
            String strPageIndex = String.valueOf(param.get("PAGE_INDEX"));
            String strPageRow   = String.valueOf(param.get("PAGE_ROW"));
            int nPageIndex = 0;
            int nPageRow   = Integer.parseInt(Constant.DEFAULT_PAGE_ROW.getCode());

            if(!StringUtils.isEmpty(strPageIndex)){
                nPageIndex = Integer.parseInt(strPageIndex);
            }
            if(!StringUtils.isEmpty(strPageRow)){
                nPageRow = Integer.parseInt(strPageRow);
            }
            param.put("START", (nPageIndex * nPageRow) + 1);
            param.put("END"  , (nPageIndex * nPageRow) + nPageRow);
            param.put("ROWS" , nPageRow);
        }

        result = dao.list(qKey, param);

        // 조회쿼리에 TOTAL COUNT 집계 컬럼 없이 카운트 쿼리 실행시 (COUNT(*) OVER() AS TOTAL_COUNT)
        if(isExistCount && totalCount > 0){
            model.setTotal(totalCount);
        }else{
            if(result.size() > 0 && result.get(0).get("TOTAL_COUNT") != null) {
                model.setTotal(Integer.parseInt(String.valueOf(result.get(0).get("TOTAL_COUNT"))));
            }
        }

        // Summary Row Append
        if(StringUtils.isNotEmpty(summaryQKey) && result.size() > 0){
            Map<String, Object> lastRowData = result.get(result.size() - 1);
            int rn = Integer.parseInt(String.valueOf(lastRowData.get("RN")));
            if(rn == model.getTotal()){
                param.remove("PAGE_INDEX");
                param.remove("PAGE_ROW");
                Map<String, Object> summaryData = (Map<String, Object>) dao.select(summaryQKey, param);
                summaryData.put("RN", Constant.SUMMARY_TITLE.getCode());

                result.add(summaryData);
            }
        }

        model.setDataList(result);

        return model;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveAccessLog(AccessLogModel model) {
        if(isLogWrite(model)){
            daoFactory.getDao().insert("common.insertAccessLog", model);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AjaxModel selectList(AjaxModel model) {
        Map param = model.getData();

        // 필수 쿼리 아이디 체크
        if(StringUtils.isEmpty((String)param.get(Constant.QUERY_KEY.getCode()))){
            model.setStatus(-1);
            model.setMsg(getMessage("E00000001")); // 조회 쿼리 ID가 존재하지 않습니다.

            return model;
        }

        AbstractDAO dao = daoFactory.getDao(getDbPoolName(param));

        // 조회쿼리
        String qKey = (String)param.get(Constant.QUERY_KEY.getCode());
        model.setDataList(dao.<Map<String, Object>>list(qKey, param));

        return model;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AjaxModel select(AjaxModel model) {
        Map param = model.getData();

        // 필수 쿼리 아이디 체크
        if(StringUtils.isEmpty((String)param.get(Constant.QUERY_KEY.getCode()))){
            model.setStatus(-1);
            model.setMsg(getMessage("E00000001")); // 조회 쿼리 ID가 존재하지 않습니다.

            return model;
        }

        AbstractDAO dao = daoFactory.getDao(getDbPoolName(param));

        // 조회쿼리
        String qKey = (String)param.get(Constant.QUERY_KEY.getCode());
        model.setData((Map<String, Object>) dao.select(qKey, param));

        return model;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AjaxModel selectCommCode(AjaxModel model) {
        model.setDataList(daoFactory.getDao().<Map<String, Object>>list("common.selectCommCode", model.getData()));
        return model;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AjaxModel selectCommCodeForCombo(AjaxModel model) {
        model.setDataList(daoFactory.getDao().<Map<String, Object>>list("common.selectCommCodeForCombo", model.getData()));
        return model;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AjaxModel selectCommCodesForCombos(AjaxModel model) {
        model.setDataList(daoFactory.getDao().<Map<String, Object>>list("common.selectCommCodesForCombos", model.getData()));
        return model;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void excelDownload(String params, HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(URLDecoder.decode(params, StandardCharsets.UTF_8.toString()));

        // 필수 쿼리 아이디 체크
        if(StringUtils.isEmpty((String)jsonObject.get(Constant.QUERY_KEY.getCode()))){
            JSONObject retObject = new JSONObject();
            retObject.put("code", "qKey가 존재하지 않습니다.");

            OutputStream outputStream = response.getOutputStream();
            outputStream.write(retObject.toJSONString().getBytes(StandardCharsets.UTF_8.toString()));
            outputStream.flush();

            return;
        }

        String qKey = (String)jsonObject.get(Constant.QUERY_KEY.getCode());
        Map<String, Object> map = (Map)jsonObject.get("params");

        AbstractDAO dao = daoFactory.getDao(getDbPoolName(map));

        // paging qKey를 이용해서 사용할때
        map.put("START", 1);
        map.put("END"  , 999);

        try{
            List<Map<String, Object>> headers = (List)jsonObject.get("headers");

            UserSessionModel sessionModel = getUesrSessionModel(request);
            if(sessionModel != null){
                addUsrIinfoToMap(sessionModel, map);
            }

            List<Map<String, Object>> resultList = dao.list(qKey, map);

            for(int i=(headers.size()-1); i >=0; i--){
                Map<String, Object> h = headers.get(i);
                if(h.containsKey("HIDDEN") && "true".equals(h.get("HIDDEN"))){
                    headers.remove(i);
                }

                String headText = (String)h.get("HEAD_TEXT");
                if(StringUtils.isNotEmpty(headText) && headText.contains("& #")){
                    headText = headText.replaceAll("& #40;", "\\(").replaceAll("& #41;", "\\)").replaceAll("& #39;", "'");
                    h.put("HEAD_TEXT", headText);
                }
            }

            ExcelWriter excelWriter = new ExcelWriter();
            excelWriter.writeSheet(headers, resultList);
            excelWriter.output(response);

        }catch(Exception e){
            logger.error("excelDownload Error : {}", e);
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AjaxModel delete(AjaxModel model) {
        List<Map<String, Object>> dataList = model.getDataList();

        if(dataList == null){
            dataList = new ArrayList<>();
        }

        if(model.getDataList() == null && model.getData() != null){
            dataList.add(model.getData());
        }

        Map map = model.getData();

        // 필수 쿼리 아이디 체크
        if(StringUtils.isEmpty((String)map.get(Constant.QUERY_KEY.getCode()))){
            model.setStatus(-1);
            model.setMsg(getMessage("E00000005")); // 삭제 쿼리 ID가 존재하지 않습니다.

            return model;
        }

        AbstractDAO dao = daoFactory.getDao(getDbPoolName(map));

        String qKey = (String)map.get(Constant.QUERY_KEY.getCode());
        for(Map<String, Object> param : dataList){
            dao.delete(qKey, param);
        }

        model.setCode("I00000004"); //삭제되었습니다.

        return model;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AjaxModel insert(AjaxModel model) {
        Map<String, Object> param = model.getData();
        // 필수 쿼리 아이디 체크
        if(StringUtils.isEmpty((String)param.get(Constant.QUERY_KEY.getCode()))){
            model.setStatus(-1);
            model.setMsg(getMessage("E00000001")); // 조회 쿼리 ID가 존재하지 않습니다.

            return model;
        }

        AbstractDAO dao = daoFactory.getDao(getDbPoolName(param));

        String qKey = (String)param.get(Constant.QUERY_KEY.getCode());

        dao.insert(qKey, param);
        model.setCode("I00000003"); //저장되었습니다.

        return model;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AjaxModel update(AjaxModel model) {
        Map<String, Object> param = model.getData();
        // 필수 쿼리 아이디 체크
        if(StringUtils.isEmpty((String)param.get(Constant.QUERY_KEY.getCode()))){
            model.setStatus(-1);
            model.setMsg(getMessage("E00000001")); // 조회 쿼리 ID가 존재하지 않습니다.

            return model;
        }

        AbstractDAO dao = daoFactory.getDao(getDbPoolName(param));

        String qKey = (String)param.get(Constant.QUERY_KEY.getCode());

        dao.update(qKey, param);
        model.setCode("I00000005"); //수정되었습니다.

        return model;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendEMail(Map mailInfo) throws Exception {
        String AUTHENTICATION = "N";
        String MAIL_ENCODING  = mailSender.getDefaultEncoding();

        String file  = (String) mailInfo.get("MAIL_FILE");
        String title = (String) mailInfo.get("TITLE");
        Map sender   = (Map) mailInfo.get("SENDER");
        Map receiver = (Map) mailInfo.get("RECEIVER");
        Map cc       = (Map) mailInfo.get("CC");

        mailInfo.put("SERVER_URL", siteName);

        String text = "";//VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, file, MAIL_ENCODING, mailInfo);

        Properties properties = new Properties();
        properties.setProperty("mail.smtp.host", mailSender.getHost());

        Session ss;
        if("Y".equals(AUTHENTICATION)) {
            properties.setProperty("mail.smtp.auth", "true");
            ss = Session.getInstance(properties, new Authenticator() {
                public PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(mailSender.getUsername(), mailSender.getPassword());
                }
            });
        }else{
            ss = Session.getDefaultInstance(properties, null);
        }

        MimeMessage message = new MimeMessage(ss);//한글 전송시 메일제목 깨지는 현상으로 MimeMessage 로 바꿈.

        Address sender_address;
        if(StringUtils.trimToEmpty((String) sender.get("NAME")).equals("")) {
            sender_address = new InternetAddress((String) sender.get("ADDRESS"));
        } else {
            sender_address = new InternetAddress((String) sender.get("ADDRESS"), (String) sender.get("NAME"), MAIL_ENCODING);
        }
        message.setHeader("content-type", "text/html;charset=" + MAIL_ENCODING);
        message.setFrom(sender_address);

        List rcvAdds = (List) receiver.get("ADDRESS");
        List rcvNms = (List) receiver.get("NAME");
        for(int i = 0; i < rcvAdds.size(); i++) {
            Address rcv_address;
            if(rcvNms != null && rcvNms.size() > i && !StringUtils.trimToEmpty((String) rcvNms.get(i)).equals("")) {
                rcv_address = new InternetAddress((String) rcvAdds.get(i), (String) rcvNms.get(i), MAIL_ENCODING);
            } else {
                rcv_address = new InternetAddress((String) rcvAdds.get(i));
            }
            message.addRecipient(Message.RecipientType.TO, rcv_address);
        }

        if(cc != null) {
            List ccAdds = (List) cc.get("ADDRESS");
            List ccNms = (List) cc.get("NAME");
            for(int i = 0; i < ccAdds.size(); i++) {
                Address cc_address;
                if(ccNms.size() > i && !StringUtils.trimToEmpty((String) ccNms.get(i)).equals("")) {
                    cc_address = new InternetAddress((String) ccAdds.get(i), (String) ccNms.get(i), MAIL_ENCODING);
                } else {
                    cc_address = new InternetAddress((String) ccAdds.get(i));
                }
                message.addRecipient(Message.RecipientType.CC, cc_address);
            }
        }

        title = StringUtils.replace(title, "\r", "");
        title = StringUtils.replace(title, "\n", "");

        message.setSubject(title, MAIL_ENCODING);//제목 인코딩
        message.setContent(text, "text/html;charset=" + MAIL_ENCODING);
        message.setSentDate(new Date());

        this.mailSender.send(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendSimpleEMail(String receiver, String title, String vmName, Map vmParam) throws Exception {
        Map<String, String> senderMap = new HashMap<>();
        senderMap.put("NAME"   , "KTNET");
        senderMap.put("ADDRESS", "goGlobal");

        Map<String, Object> receiverMap = new HashMap<>();
        List<String> receiverAddr = Collections.singletonList(receiver);
        receiverMap.put("ADDRESS", receiverAddr);

        ((Map<String, Object>) vmParam).put("TITLE"          , title);
        ((Map<String, Object>) vmParam).put("SENDER"         , senderMap);
        ((Map<String, Object>) vmParam).put("RECEIVER"       , receiverMap);
        ((Map<String, Object>) vmParam).put("MAIL_FILE"      , vmName);

        sendEMail(vmParam);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AjaxModel deleteList(AjaxModel model) {
        Map<String, Object> param = model.getData();

        // 필수 쿼리 아이디 체크
        if(StringUtils.isEmpty((String)param.get(Constant.QUERY_KEY.getCode()))){
            model.setStatus(-1);
            model.setMsg(getMessage("E00000005")); // 삭제 쿼리 ID가 존재하지 않습니다.

            return model;
        }

        List<Map<String, Object>> dataList = model.getDataList();

        if(dataList == null){
            dataList = new ArrayList<>();
        }

        if(model.getDataList() == null && model.getData() != null){
            dataList.add(model.getData());
        }

        AbstractDAO dao = daoFactory.getDao(getDbPoolName(param));

        String qKey = (String)param.get(Constant.QUERY_KEY.getCode());
        for(Map<String, Object> delData : dataList){
            dao.delete(qKey, delData);
        }

        model.setCode("I00000004"); //삭제되었습니다.

        return model;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> selectUserIpAccess(String userId) {
        return daoFactory.getDao().list("common.selectUserIpAccess", userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserSessionModel getUesrSessionModel(HttpServletRequest request) {
        String sessionDiv = request.getHeader("sessiondiv"); // ajax 용
        if(sessionDiv == null) sessionDiv = request.getParameter("MENU_DIV");   // 메뉴 용
        if(sessionDiv == null) sessionDiv = request.getParameter("sessionDiv");
        if(sessionDiv == null) sessionDiv = request.getParameter(Constant.ACTION_MENU_DIV.getCode());
        if(sessionDiv == null) sessionDiv = (request.getAttribute("sessionDiv") == null ? null : (String)request.getAttribute("sessionDiv")); // AdminServlet 용

        HttpSession session = request.getSession();
        String sessionKey = Constant.SESSION_KEY_USR.getCode();
        if(Constant.ADM_SESSION_DIV.getCode().equals(sessionDiv)){
            sessionKey = Constant.SESSION_KEY_ADM.getCode();

        }

        return (UserSessionModel)session.getAttribute(sessionKey);
    }

    /**
     * 파라미터네서 dbPoolName을 반환
     * @param param
     * @return
     */
    private String getDbPoolName(Map param){
        return param.get("dbPoolName") == null ? "default" : (String)param.get("dbPoolName");
    }

    /**
     * {@inheritDoc}
     * @param model
     * @return
     */
    @Override
    public boolean isLogWrite(AccessLogModel model) {
        if(logMngList == null){
            loadLogMngModel();
        }

        boolean screenIdCompare = true;
        boolean urlCompare      = true;
        boolean rmkCompare      = true;
        for(LogMngModel logMngModel : logMngList){
            if(logMngModel.getScreenId() != null && !"*".equals(logMngModel.getScreenId()) &&
                    !logMngModel.getScreenId().equals(model.getScreenId())){
                continue;

            }

            if(logMngModel.getUri() != null && !"*".equals(logMngModel.getUri()) &&
                    !logMngModel.getUri().equals(model.getUri())){
                continue;

            }

            if(logMngModel.getRmk() != null && !"*".equals(logMngModel.getRmk()) &&
                    !logMngModel.getRmk().equals(model.getRmk().replaceAll(" ", ""))){
                continue;
            }

            if(logMngModel.getScreenId() == null || "*".equals(logMngModel.getScreenId()) ||
                    logMngModel.getScreenId().equals(model.getScreenId())){
                screenIdCompare = false;
            }

            if(logMngModel.getUri() == null || "*".equals(logMngModel.getUri()) ||
                    logMngModel.getUri().equals(model.getUri())){
                urlCompare = false;
            }

            if(logMngModel.getRmk() == null || "*".equals(logMngModel.getRmk()) ||
                    logMngModel.getRmk().equals(model.getRmk().replaceAll(" ", ""))){
                rmkCompare = false;
            }

            if(!screenIdCompare && !urlCompare && !rmkCompare){
                logger.debug("로그 제외 : {}, {}, {} ",
                        logMngModel.getScreenId() + " : " + model.getScreenId(),
                        logMngModel.getUri() + " : " + model.getUri(),
                        logMngModel.getRmk() + " : " + model.getRmk().replaceAll(" ", "")
                );
                return false;
            }
        }

        return true;
    }

    /**
     * 로그필터관리 조회
     */
    public void loadLogMngModel(){
        logMngList = daoFactory.getDao().list("log.selectCmmLogMngList");
    }

    @Override
    public AjaxModel selectCmmLogTest(AjaxModel model) throws SQLException {
        Map<String, Object> param = model.getData();
        List<String> exclude = Arrays.asList("titleParameter", "ACTION_MENU_ID", "ACTION_MENU_NM", "ACTION_MENU_DIV", "ACTION_NM", "qKey", "dbPoolName", "PAGE_INDEX", "PAGE_ROW", "sessionDiv");

        String strPageIndex = String.valueOf(param.get("PAGE_INDEX"));
        String strPageRow   = String.valueOf(param.get("PAGE_ROW"));
        int nPageIndex = 0;
        int nPageRow   = Integer.parseInt(Constant.DEFAULT_PAGE_ROW.getCode());

        if(!StringUtils.isEmpty(strPageIndex)){
            nPageIndex = Integer.parseInt(strPageIndex);
        }
        if(!StringUtils.isEmpty(strPageRow)){
            nPageRow = Integer.parseInt(strPageRow);
        }
        param.put("START", (nPageIndex * nPageRow) + 1);
        param.put("END"  , (nPageIndex * nPageRow) + nPageRow);
        param.put("ROWS" , nPageRow);

        param.put("USER_ID" , model.getUserSessionModel().getUserId());

        String searchCol = (String)param.get("SEARCH_COL1");

        if(param.get("SEARCH_TXT1") != null){
            param.put(searchCol, param.get("SEARCH_TXT1"));
        }

        List<Map<String, Object>> result = new ArrayList<>();
        List<Map<String, Object>> list = daoFactory.getDao().list("common.selectLogList", param);

        for (Map<String, Object> map : list) {
            Clob clob = (Clob) map.get("PARAM");

            if (clob != null) {
                int size = (int) clob.length();
                String value = clob.getSubString(1L, size);
                Gson gson = new Gson();
                JsonReader reader = new JsonReader(new StringReader(value));
                reader.setLenient(true);
                Map paramData = gson.fromJson(reader, Map.class);

                Map<String, Object> titleParam = (Map) paramData.get("titleParameter");
                StringBuffer logPramBuffer = new StringBuffer();
                if (titleParam != null) {
                    for (Map.Entry<String, Object> entry : titleParam.entrySet()) {
                        String title = (String) entry.getValue();
                        String val = paramData.get(entry.getKey()) == null ? "" : String.valueOf(paramData.get(entry.getKey()));
                        if (StringUtils.isNotEmpty(title)) {
                            logPramBuffer.append(title).append("[").append(entry.getKey()).append("]:").append(val).append(", ");
                        }
                    }
                }
                if (logPramBuffer.length() > 2) {
                    logPramBuffer = new StringBuffer(logPramBuffer.substring(0, logPramBuffer.length() - 2));
                }

                for(String str : exclude){
                    paramData.remove(str);
                }

                map.put("PARAM", logPramBuffer);
                map.put("ALL_PARAM", gson.toJson(paramData));
            }

            result.add(map);
        }

        if(result.size() > 0){
            model.setTotal(Integer.parseInt(String.valueOf(result.get(0).get("TOTAL_COUNT"))));
        }else{
            model.setTotal(0);
        }
        model.setDataList(result);

        return model;
    }

    /**
     * {@inheritDoc}
     * @param model
     */
    @Override
    public AjaxModel selectIndexLoad(AjaxModel model) {
        Map<String, Object> retMap = new HashMap<>();

        // 공지사항 리스트
        List<Map<String, Object>> noticeList = daoFactory.getDao().list("common.selectNoticeList");

        retMap.put("noticeList", noticeList);

        // 공지사항 리스트
        List<Map<String, Object>> newsList = daoFactory.getDao().list("common.selectNewsList");

        retMap.put("newsList", newsList );

        // 공지사항 팝업 리스트
        List<Map<String, Object>> popupNoticeList = daoFactory.getDao().list("common.selectPopupNoticeList");

        retMap.put("popupNoticeList", popupNoticeList);

        model.setData(retMap);

        return model;
    }
    
    /**
     * {@inheritDoc}
     * @param docNm
     */
    @Override
	public Map<String,String> createDocId(String docNm) {
		Map<String,String> rtn = new HashMap<String,String>();
		rtn.put("DOCNM", docNm);
		rtn.put("RGSTTM", DateUtil.getToday("yyyyMMdd"));
		
		daoFactory.getDao().update("common.updateDocId", rtn);
		
		rtn.put("SQN", String.valueOf(daoFactory.getDao().select("common.selectDocId", docNm)));
		
		return rtn;
	}
	
    /**
     * {@inheritDoc}
     * @param docNm
     */
    @Override
	public void createDocNm(String docNm) throws Exception {
		daoFactory.getDao().insert("common.insertDocId", docNm);
	}

    /**
     * {@inheritDoc}
     * @param map
     * @throws Exception
     */
    @Override
    public void addUsrIinfoToMap(UserSessionModel userSessionModel, Map map) throws Exception {
        Map<String, String> tempMap = BeanUtils.describe(userSessionModel);

        for ( Map.Entry< String, String> entry : tempMap.entrySet() ) {
            String notTarget = "menuModelList,userPw,class";
            if(notTarget.contains(entry.getKey())) {
                continue;
            }

            String key = entry.getKey();
            String val = String.valueOf(ObjectUtils.defaultIfNull(entry.getValue(), ""));
            map.put("SS"+key, val);
        }
    }
}
