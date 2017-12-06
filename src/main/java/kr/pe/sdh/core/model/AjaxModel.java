package kr.pe.sdh.core.model;

import kr.pe.sdh.common.model.UserSessionModel;

import java.util.List;
import java.util.Map;

/**
 * Client 와 Server의 요청/응답 처리시 규격 Model
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
public class AjaxModel {

    private Map<String, Object> data;
    private List<Map<String, Object>> dataList;

    private int total = 0;
    private int status = 0;

    private String code = "";
    private String msg  = "";

    private UserSessionModel userSessionModel;

    public AjaxModel() {

    }

    public AjaxModel(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public Map<String, Object> getData() {
        return data;
    }
    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public List<Map<String, Object>> getDataList() {
        return dataList;
    }
    public void setDataList(List<Map<String, Object>> dataList) {
        this.dataList = dataList;
    }

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public UserSessionModel getUserSessionModel() {
        return userSessionModel;
    }

    public void setUserSessionModel(UserSessionModel userSessionModel) {
        this.userSessionModel = userSessionModel;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        if(!code.startsWith("I")){
            setStatus(-1);
        }
        this.code = code;
    }

    @Override
    public String toString() {
        return "AjaxModel{" +
                "data=" + data +
                ", dataList=" + dataList +
                ", total=" + total +
                ", status=" + status +
                ", code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", userSessionModel=" + userSessionModel +
                '}';
    }
}
