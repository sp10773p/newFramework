package kr.pe.sdh.core.model;

import com.google.gson.Gson;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 사용자 Model
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
@Alias("usrSessionModel")
public class UsrSessionModel implements Serializable{
    /*사용자ID*/
    private String userId;
    /*사용자명*/
    private String userNm;
    /*패스워드*/
    private String userPw;
    /*사용자구분*/
    private String userDiv;
    /*권한코드*/
    private String authCd;
    /*전화번호*/
    private String telNo;
    /*핸드폰번호*/
    private String hpNo;
    /*이메일*/
    private String email;
    /*최초로그인*/
    private String loginStart;
    /*최종로그인*/
    private String loginLast;
    /*최종로그인*/
    private String loginLastStr;
    /*로그인에러*/
    private int loginError;
    /*이전비밀번호*/
    private String pwPrior;
    /*비밀번호변경일*/
    private String pwChange;
    /*비밀번호변경주기*/
    private String pwUpdate;
    /*비밀번호변경기간*/
    private String pwPeriod;
    /*사용여부*/
    private String useChk;
    /*사용자 상태*/
    private String userStatus;
    /*사업자등록번호*/
    private String bizNo;
    /*사업자구분*/
    private String bizDiv;
    /*담당자명*/
    private String chargeNm;
    /*등록자ID*/
    private String regId;
    /*등록일자*/
    private String regDtm;
    /*수정자ID*/
    private String modId;
    /*수정일자*/
    private String modDtm;
    /*관세사신고인부호*/
    private String applicantId;
    /*통관고유부호*/
    private String tgNo;
    /*대표자명*/
    private String repNm;
    /*우편번호*/
    private String zipCd;
    /*주소*/
    private String address;

    /*UTH 사용자 ID*/
    private String uthUserId;

	/* 사용자 접속 IP */
    private String userIp;

    List<MenuModel> menuModelList;

    Map<String, List<String>> menuBtnAuth;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserNm() {
        return userNm;
    }

    public void setUserNm(String userNm) {
        this.userNm = userNm;
    }

    public String getUserPw() {
        return userPw;
    }

    public void setUserPw(String userPw) {
        this.userPw = userPw;
    }

    public String getUserDiv() {
        return userDiv;
    }

    public void setUserDiv(String userDiv) {
        this.userDiv = userDiv;
    }

    public String getAuthCd() {
        return authCd;
    }

    public void setAuthCd(String authCd) {
        this.authCd = authCd;
    }

    public String getTelNo() {
        return telNo;
    }

    public void setTelNo(String telNo) {
        this.telNo = telNo;
    }

    public String getHpNo() {
        return hpNo;
    }

    public void setHpNo(String hpNo) {
        this.hpNo = hpNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLoginStart() {
        return loginStart;
    }

    public void setLoginStart(String loginStart) {
        this.loginStart = loginStart;
    }

    public String getLoginLast() {
        return loginLast;
    }

    public void setLoginLast(String loginLast) {
        this.loginLast = loginLast;
    }

    public int getLoginError() {
        return loginError;
    }

    public void setLoginError(int loginError) {
        this.loginError = loginError;
    }

    public String getPwPrior() {
        return pwPrior;
    }

    public void setPwPrior(String pwPrior) {
        this.pwPrior = pwPrior;
    }

    public String getPwChange() {
        return pwChange;
    }

    public void setPwChange(String pwChange) {
        this.pwChange = pwChange;
    }

    public String getPwUpdate() {
        return pwUpdate;
    }

    public void setPwUpdate(String pwUpdate) {
        this.pwUpdate = pwUpdate;
    }

    public String getPwPeriod() {
        return pwPeriod;
    }

    public void setPwPeriod(String pwPeriod) {
        this.pwPeriod = pwPeriod;
    }

    public String getUseChk() {
        return useChk;
    }

    public void setUseChk(String useChk) {
        this.useChk = useChk;
    }

    public String getBizNo() {
        return bizNo;
    }

    public void setBizNo(String bizNo) {
        this.bizNo = bizNo;
    }

    public String getBizDiv() {
        return bizDiv;
    }

    public void setBizDiv(String bizDiv) {
        this.bizDiv = bizDiv;
    }

    public String getChargeNm() {
        return chargeNm;
    }

    public void setChargeNm(String chargeNm) {
        this.chargeNm = chargeNm;
    }

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public String getRegDtm() {
        return regDtm;
    }

    public void setRegDtm(String regDtm) {
        this.regDtm = regDtm;
    }

    public String getModId() {
        return modId;
    }

    public void setModId(String modId) {
        this.modId = modId;
    }

    public String getModDtm() {
        return modDtm;
    }

    public void setModDtm(String modDtm) {
        this.modDtm = modDtm;
    }

    public String getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(String applicantId) {
        this.applicantId = applicantId;
    }
    
    public String getTgNo() {
		return tgNo;
	}

	public void setTgNo(String tgNo) {
		this.tgNo = tgNo;
	}
	
    public String getRepNm() {
		return repNm;
	}

	public void setRepNm(String repNm) {
		this.repNm = repNm;
	}

	public String getZipCd() {
		return zipCd;
	}

	public void setZipCd(String zipCd) {
		this.zipCd = zipCd;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }

    public List<MenuModel> getMenuModelList() {
        return menuModelList;
    }

    public void setMenuModelList(List<MenuModel> menuModelList) {
        this.menuModelList = menuModelList;
    }

    public Map<String, List<String>> getMenuBtnAuth() {
        return menuBtnAuth;
    }

    public String getMenuBtnAuthJson(){
        return new Gson().toJson(menuBtnAuth);
    }

    public void setMenuBtnAuth(Map<String, List<String>> menuBtnAuth) {
        this.menuBtnAuth = menuBtnAuth;
    }

    public List<String> getMenuBtnAuthList(String menuId){
        return this.menuBtnAuth.get(menuId);
    }

    public String getLoginLastStr() {
        return loginLastStr;
    }

    public void setLoginLastStr(String loginLastStr) {
        this.loginLastStr = loginLastStr;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getUthUserId() {
        return uthUserId;
    }

    public void setUthUserId(String uthUserId) {
        this.uthUserId = uthUserId;
    }

    @Override
    public String toString() {
        return "UsrSessionModel{" +
                "userId='" + userId + '\'' +
                ", userNm='" + userNm + '\'' +
                ", userPw='" + userPw + '\'' +
                ", userDiv='" + userDiv + '\'' +
                ", authCd='" + authCd + '\'' +
                ", telNo='" + telNo + '\'' +
                ", hpNo='" + hpNo + '\'' +
                ", email='" + email + '\'' +
                ", loginStart='" + loginStart + '\'' +
                ", loginLast='" + loginLast + '\'' +
                ", loginLastStr='" + loginLastStr + '\'' +
                ", loginError=" + loginError +
                ", pwPrior='" + pwPrior + '\'' +
                ", pwChange='" + pwChange + '\'' +
                ", pwUpdate='" + pwUpdate + '\'' +
                ", pwPeriod='" + pwPeriod + '\'' +
                ", useChk='" + useChk + '\'' +
                ", userStatus='" + userStatus + '\'' +
                ", bizNo='" + bizNo + '\'' +
                ", bizDiv='" + bizDiv + '\'' +
                ", chargeNm='" + chargeNm + '\'' +
                ", regId='" + regId + '\'' +
                ", regDtm='" + regDtm + '\'' +
                ", modId='" + modId + '\'' +
                ", modDtm='" + modDtm + '\'' +
                ", applicantId='" + applicantId + '\'' +
                ", tgNo='" + tgNo + '\'' +
                ", repNm='" + repNm + '\'' +
                ", zipCd='" + zipCd + '\'' +
                ", address='" + address + '\'' +
                ", uthUserId='" + uthUserId + '\'' +
                ", userIp='" + userIp + '\'' +
                ", menuModelList=" + menuModelList +
                ", menuBtnAuth=" + menuBtnAuth +
                '}';
    }
}
