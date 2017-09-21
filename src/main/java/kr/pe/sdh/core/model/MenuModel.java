package kr.pe.sdh.core.model;

import org.apache.ibatis.type.Alias;

import java.io.Serializable;

/**
 * 메뉴 Model
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
@Alias("menuModel")
public class MenuModel implements Serializable{

    /** 메뉴코드 **/
    private String menuId;
    /** 상위메뉴코드 **/
    private String pmenuId;
    /** 메뉴명 **/
    private String menuNm;
    /** 메뉴설명 **/
    private String menuDc;
    /** 경로 **/
    private String menuPath;
    /** 주소 **/
    private String menuUrl;
    /** 레벨 **/
    private String menuLevel;
    /** 구분 **/
    private String menuDiv;
    /** 링크여부 **/
    private String linkYn;
    /** 순서 **/
    private int menuOrdr;
    /** 권한코드 **/
    private String authCd;
    /** 메뉴코드01 **/
    private String menuCod1;
    /** 메뉴코드02 **/
    private String menuCod2;
    /** 메뉴코드03 **/
    private String menuCod3;
    /** 메뉴코드04 **/
    private String menuCod4;
    /** 등록자ID **/
    private String regId;
    /** 등록일시 **/
    private String regDtm;
    /** 수정자ID **/
    private String modId;
    /** 수정일시 **/
    private String modDtm;
    /** DASH 경로 **/
    private String dashPath;
    /** DASH 주소 **/
    private String dashUrl;

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getPmenuId() {
        return pmenuId;
    }

    public void setPmenuId(String pmenuId) {
        this.pmenuId = pmenuId;
    }

    public String getMenuNm() {
        return menuNm;
    }

    public void setMenuNm(String menuNm) {
        this.menuNm = menuNm;
    }

    public String getMenuDc() {
        return menuDc;
    }

    public void setMenuDc(String menuDc) {
        this.menuDc = menuDc;
    }

    public String getMenuPath() {
        return menuPath;
    }

    public void setMenuPath(String menuPath) {
        this.menuPath = menuPath;
    }

    public String getMenuUrl() {
        return menuUrl;
    }

    public void setMenuUrl(String menuUrl) {
        this.menuUrl = menuUrl;
    }

    public String getMenuLevel() {
        return menuLevel;
    }

    public void setMenuLevel(String menuLevel) {
        this.menuLevel = menuLevel;
    }

    public String getMenuDiv() {
        return menuDiv;
    }

    public void setMenuDiv(String menuDiv) {
        this.menuDiv = menuDiv;
    }

    public String getLinkYn() {
        return linkYn;
    }

    public void setLinkYn(String linkYn) {
        this.linkYn = linkYn;
    }

    public int getMenuOrdr() {
        return menuOrdr;
    }

    public void setMenuOrdr(int menuOrdr) {
        this.menuOrdr = menuOrdr;
    }

    public String getAuthCd() {
        return authCd;
    }

    public void setAuthCd(String authCd) {
        this.authCd = authCd;
    }

    public String getMenuCod1() {
        return menuCod1;
    }

    public void setMenuCod1(String menuCod1) {
        this.menuCod1 = menuCod1;
    }

    public String getMenuCod2() {
        return menuCod2;
    }

    public void setMenuCod2(String menuCod2) {
        this.menuCod2 = menuCod2;
    }

    public String getMenuCod3() {
        return menuCod3;
    }

    public void setMenuCod3(String menuCod3) {
        this.menuCod3 = menuCod3;
    }

    public String getMenuCod4() {
        return menuCod4;
    }

    public void setMenuCod4(String menuCod4) {
        this.menuCod4 = menuCod4;
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

    public String getDashPath() {
        return dashPath;
    }

    public void setDashPath(String dashPath) {
        this.dashPath = dashPath;
    }

    public String getDashUrl() {
        return dashUrl;
    }

    public void setDashUrl(String dashUrl) {
        this.dashUrl = dashUrl;
    }
}
