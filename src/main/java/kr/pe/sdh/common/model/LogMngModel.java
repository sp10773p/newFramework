package kr.pe.sdh.common.model;

import org.apache.ibatis.type.Alias;

import java.io.Serializable;

/**
 * 로그필터 관리 Model
 * @author 성동훈
 * @version 1.0
 * @since 2017-02-27
 * @see
 * <pre>
 * == 개정이력(Modification Information) ==
 *
 * 수정일      수정자  수정내용
 * ----------- ------- ---------------------------
 * 2017-02-27  성동훈  최초 생성
 * </pre>
 */
@Alias("logMngModel")
public class LogMngModel implements Serializable {
    private String screenId;
    private String uri;
    private String rmk;

    public String getScreenId() {
        return screenId;
    }

    public void setScreenId(String screenId) {
        this.screenId = screenId;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getRmk() {
        return rmk;
    }

    public void setRmk(String rmk) {
        this.rmk = rmk;
    }

    @Override
    public String toString() {
        return "LogMngModel{" +
                "screenId='" + screenId + '\'' +
                ", uri='" + uri + '\'' +
                ", rmk='" + rmk + '\'' +
                '}';
    }
}
