package kr.pe.sdh.core.controller;

import kr.pe.sdh.core.model.AjaxModel;
import kr.pe.sdh.core.service.CommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

/**
 * 공통 처리 Controller
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
@Controller
public class CommonController {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource(name = "commonService")
    private CommonService commonService;

    /**
     * Client에서 사용할 메세지 조회
     * @param model
     * @return Message Object
     */
    @RequestMapping(value = "/common/getMessage")
    @ResponseBody
    public AjaxModel getMessageForClinet(AjaxModel model) {
        model.setData(commonService.getMessageData());
        return model;
    }

    /**
     * 그리드 페이징 처리가 포함된 조회
     * @param model
     * @return
     */
    @RequestMapping(value = "/common/selectGridPagingList")
    @ResponseBody
    public AjaxModel selectGridPagingList(AjaxModel model) {
        return commonService.selectGridPagingList(model);
    }

    /**
     * 그리드 페이징 처리가 포함된 우편번호 조회
     * @param model
     * @return
     */
    @RequestMapping(value = "/common/selectZipcodePagingList")
    @ResponseBody
    public AjaxModel selectZipcodePagingList(AjaxModel model) {
        return commonService.selectGridPagingList(model);
    }

    /**
     * 리스트 조회
     * @param model
     * @return
     */
    @RequestMapping(value = "/common/selectList")
    @ResponseBody
    public AjaxModel selectList(AjaxModel model) {
        return commonService.selectList(model);
    }

    /**
     * 단일 조회
     * @param model
     * @return
     */
    @RequestMapping(value = "/common/select")
    @ResponseBody
    public AjaxModel select(AjaxModel model) {
        return commonService.select(model);
    }

    /**
     * 공통코드 조회
     * @param model
     * @return
     */
    @RequestMapping(value = "/common/selectCommCode")
    @ResponseBody
    public AjaxModel selectCommCode(AjaxModel model) {
        return commonService.selectCommCode(model);
    }

    /**
     * ComboBox(Select Box)처리를 위한 공통코드 조회
     * @param model
     * @return
     */
    @RequestMapping(value = "/common/selectCommCodeForCombo")
    @ResponseBody
    public AjaxModel selectCommCodeForCombo(AjaxModel model) {
        return commonService.selectCommCodeForCombo(model);
    }

    /**
     * N개의 ComboBox(Select Box)처리를 위한 공통코드 조회
     * @param model
     * @return
     */
    @RequestMapping(value = "/common/selectCommCodesForCombos")
    @ResponseBody
    public AjaxModel selectCommCodesForCombos(AjaxModel model) {
        return commonService.selectCommCodesForCombos(model);
    }

    /**
     * 그리드 엑셀 다운로드
     * @param params
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/common/excelDownload")
    public void excelDownload(@RequestParam(value = "params") String params, HttpServletRequest request, HttpServletResponse response) throws Exception {
        commonService.excelDownload(params, request, response);
    }

    /**
     * Delete 처리
     * @param model
     * @return
     */
    @RequestMapping(value = "/common/delete")
    @ResponseBody
    public AjaxModel delete(AjaxModel model) {
        return commonService.delete(model);
    }

    /**
     * Insert 처리
     * @param model
     * @return
     */
    @RequestMapping(value = "/common/insert")
    @ResponseBody
    public AjaxModel insert(AjaxModel model) {
        return commonService.insert(model);
    }

    /**
     * Update 처리
     * @param model
     * @return
     */
    @RequestMapping(value = "/common/update")
    @ResponseBody
    public AjaxModel update(AjaxModel model) {
        return commonService.update(model);
    }

    /**
     * N건 Delete 처리 ( 그리드 삭제 )
     * @param model
     * @return
     */
    @RequestMapping(value = "/common/deleteList")
    @ResponseBody
    public AjaxModel deleteList(AjaxModel model) {
        return commonService.deleteList(model);
    }


    @RequestMapping(value = "/common/selectCmmLogTest")
    @ResponseBody
    public AjaxModel selectCmmLogTest(AjaxModel model) throws SQLException {
        return commonService.selectCmmLogTest(model);
    }

    /**
     * [세션체크 무시] 리스트 조회
     * @param model
     * @return
     */
    @RequestMapping(value = "/common/selectListNonSession")
    @ResponseBody
    public AjaxModel selectNoticeList(AjaxModel model) {
        return commonService.selectList(model);
    }

    /**
     * [세션체크 무시] 단일 조회
     * @param model
     * @return
     */
    @RequestMapping(value = "/common/selectNonSession")
    @ResponseBody
    public AjaxModel selectNonSession(AjaxModel model) {
        return commonService.select(model);
    }

    /**
     * 사용자 index 화면로드시 조회
     * @param model
     * @return
     */
    @RequestMapping(value = "/common/selectIndexLoad")
    @ResponseBody
    public AjaxModel selectIndexLoad(AjaxModel model) {
        return commonService.selectIndexLoad(model);
    }


    /**
     * 메인 요약 조회
     * @param model
     * @return
     */
    @RequestMapping(value = "/common/selectDecSummary")
    @ResponseBody
    public AjaxModel selectDecSummary(AjaxModel model) {
        return commonService.selectDecSummary(model);
    }

}
