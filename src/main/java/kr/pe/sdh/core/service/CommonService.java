package kr.pe.sdh.core.service;

import kr.pe.sdh.core.model.AccessLogModel;
import kr.pe.sdh.core.model.AjaxModel;
import kr.pe.sdh.core.model.UsrSessionModel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 공통 서비스 처리 추상 클래스
 * @author 성동훈
 * @since 2017-01-03
 * @version 1.0
 * @see CommonServiceImpl
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
public interface CommonService {
    /**
     * 전체 메시지 조회
     * @return 메세지 리스트
     */
    public List<Map<String, String>> getMessageAll();

    /**
     * 메세지리스트를 객체화 하여 리턴
     * @return
     */
    public Map<String, Object> getMessageData();

    /**
     * Message Strorage 생성
     * @param message
     */
    void loadMessage(Map<String, Object> message);

    /**
     * 파라미터의 메시지코드에 대한 메시지를 리턴
     * @param code
     * @return
     */
    public String getMessage(String code);

    /**
     * 파라미터의 메시지코드에 대한 메시지를 리턴
     * @param code
     * @param arg
     * @return
     */
    public String getMessage(String code, String arg);

    /**
     * 파라미터의 메시지코드에 대한 메시지를 리턴
     * @param code
     * @param args
     * @return
     */
    public String getMessage(String code, List<String> args);

    /**
     * 파라미터의 메시지코드에 대한 메시지를 리턴
     * @param code
     * @param args
     * @return
     */
    public String getMessage(String code, String[] args);

    /**
     * 그리드 페이징리스트를 조회하는 공통 메서드
     * @param model
     * @return 페이징 리스트
     */
    public AjaxModel selectGridPagingList(AjaxModel model);

    /**
     * 접속, 기능에 대한 로그 저장
     * @param model
     */
    public void saveAccessLog(AccessLogModel model);

    /**
     * 리스트를 조회하는 공통 메서드
     * @param model
     * @return List
     */
    public AjaxModel selectList(AjaxModel model);

    /**
     * 단일행을 조회하는 공통 메서드
     * @param model
     * @return Object
     */
    public AjaxModel select(AjaxModel model);

    /**
     * 공통코드를 조회
     * @param model
     * @return List
     */
    public AjaxModel selectCommCode(AjaxModel model);

    /**
     * 공통코드를 조회 ( combo 용)
     * @param model
     * @return List
     */
    public AjaxModel selectCommCodeForCombo(AjaxModel model);

    /**
     * 공통코드를 조회 ( combo 용)
     * @param model
     * @return List
     */
    public AjaxModel selectCommCodesForCombos(AjaxModel model);
    
    /**
     * 그리드 엑셀 다운로드
     * @param params
     * @param request
     *@param response  @throws Exception
     */
    public void excelDownload(String params, HttpServletRequest request, HttpServletResponse response) throws Exception;

    /**
     * Delete 쿼리를 실행하는 공통 메서드
     * @param model (qKey 필수)
     * @return
     */
    public AjaxModel delete(AjaxModel model);

    /**
     * Insert 쿼리를 실행하는 공통 메서드
     * @param model (qKey 필수)
     * @return
     */
    public AjaxModel insert(AjaxModel model);

    /**
     * Update 쿼리를 실행하는 공통 메서드
     * @param model (qKey 필수)
     * @return
     */
    public AjaxModel update(AjaxModel model);

    /**
     * 메일 전송
     * @param vo ( MAIL_FILE, TITLE, SENDER, RECEIVER, CC)
     * @throws Exception
     */
    void sendEMail(Map vo) throws Exception;

    /**
     * 받는사람이 한명일때 간단하게 메일전송
     * @param receiver 받는사람메일 주소
     * @param title 메일 제목
     * @param vmName 메일포멧 Velocity 파일명
     * @param vmParam Velocity 파라미터
     * @throws Exception
     */
    void sendSimpleEMail(String receiver, String title, String vmName, Map vmParam) throws Exception;

    /**
     * Ajax의 dataList의 데이터를 data의 qKey를 실행하여 삭제하는 공통 메서드
     * @param model (qKey 필수)
     * @return
     */
    AjaxModel deleteList(AjaxModel model);

    /***
     * 접속IP권한체크
     * @param userId
     * @return
     */
    List<String> selectUserIpAccess(String userId);

    /**
     * 사이트별 User session 을 반환
     * (사용자 or 어드민)
     * @param request
     * @return
     */
    public UsrSessionModel getUsrSessionModel(HttpServletRequest request);

    /**
     * 로그 제외 여부
     * @param model
     * @return
     */
    boolean isLogWrite(AccessLogModel model);

    /**
     * 로그필터관리 조회
     */
    void loadLogMngModel();

    /**
     * 메인 요약 조회
     * @param model
     * @return
     */
    AjaxModel selectDecSummary(AjaxModel model);

    AjaxModel selectCmmLogTest(AjaxModel model) throws SQLException;


    /**
     * 사용자 index 화면로드시 조회
     * @param model
     * @return
     */
    AjaxModel selectIndexLoad(AjaxModel model);
    
    /**
     * 업무 테이블별 저장일련번호 생성
     * @param docNm
     * @return
     */
    Map<String,String> createDocId(String docNm);
	
	/**
	 * 업무 테이블별 저장일련번호 테이블 초기화
	 * @param docNm
	 * @throws Exception
	 */
	void createDocNm(String docNm) throws Exception;

    /**
     * 세션정보 셋팅
     * @param usrSessionModel
     * @param map
     * @throws Exception
     */
    void addUsrIinfoToMap(UsrSessionModel usrSessionModel, Map map) throws Exception;
}
