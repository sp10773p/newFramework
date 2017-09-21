package kr.pe.sdh.core.model;

import org.apache.ibatis.type.Alias;

/**
 * AccessLog 처리 Model
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
@Alias(value = "accessLogModel")
public class AccessLogModel {
    private String sid;
    private String sessionId;
    private String logDtm;
    private String logDiv;
    private String userId;
    private String loginIp;
    private String screenId;
    private String screenNm;
    private String apiId;
    private String apiKey;
    private String detailCnt;
    private String uri;
    private String rmk;
    private String param;

    public String getLogDtm() {
        return logDtm;
    }

    public void setLogDtm(String logDtm) {
        this.logDtm = logDtm;
    }

    public String getLogDiv() {
        return logDiv;
    }

    public void setLogDiv(String logDiv) {
        this.logDiv = logDiv;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public String getScreenId() {
        return screenId;
    }

    public void setScreenId(String screenId) {
        this.screenId = screenId;
    }

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getDetailCnt() {
        return detailCnt;
    }

    public void setDetailCnt(String detailCnt) {
        this.detailCnt = detailCnt;
    }

    public String getRmk() {
        return rmk;
    }

    public void setRmk(String rmk) {
        this.rmk = rmk;
    }

    public String getScreenNm() {
        return screenNm;
    }

    public void setScreenNm(String screenNm) {
        this.screenNm = screenNm;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        return "AccessLogModel{" +
                "sid='" + sid + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", logDtm='" + logDtm + '\'' +
                ", logDiv='" + logDiv + '\'' +
                ", userId='" + userId + '\'' +
                ", loginIp='" + loginIp + '\'' +
                ", screenId='" + screenId + '\'' +
                ", screenNm='" + screenNm + '\'' +
                ", apiId='" + apiId + '\'' +
                ", apiKey='" + apiKey + '\'' +
                ", detailCnt='" + detailCnt + '\'' +
                ", uri='" + uri + '\'' +
                ", rmk='" + rmk + '\'' +
                ", param='" + param + '\'' +
                '}';
    }
}
