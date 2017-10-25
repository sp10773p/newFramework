package kr.pe.sdh.common.service;


import kr.pe.sdh.core.model.AjaxModel;
import kr.pe.sdh.core.model.UserSessionModel;

/**
 * 시스템관리 > 사용자관리 추상 클래스
 * @author 성동훈
 * @since 2017-01-05
 * @version 1.0
 * @see UserServiceImpl
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
public interface UserService {
	/**
	 * 사용자 조회
	 *  - 세션 설정 용도
	 * @param usrId									사용자 ID
	 * @return UserSessionModel
	 * @throws Exception
	 */
	UserSessionModel selectUserSessionInfo(String usrId, String sessionDiv) throws Exception;

	/**
	 * 사용자 리스트 조회
	 * @param model
	 * @return
	 * @throws Exception
	 */
    AjaxModel selectUsrList(AjaxModel model) throws Exception;

	/**
	 * 사용자 조회
	 * @param model
	 * @return
	 * @throws Exception
	 */
    AjaxModel selectUsr(AjaxModel model) throws Exception;

	/**
	 * 사용자 저장
	 * @param model
	 * @return
	 * @throws Exception
	 */
	AjaxModel saveUser(AjaxModel model) throws Exception;

	/**
	 * 비밀번호 초기화
	 * @param model
	 * @return
	 * @throws Exception
	 */
    AjaxModel saveInitPass(AjaxModel model) throws Exception;

	/**
	 * 사용자 승인처리
	 * @param model
	 * @return
	 * @throws Exception
	 */
    AjaxModel saveUserApprove(AjaxModel model) throws Exception;

	/**
	 * API 키 생성
	 * @param model
	 * @return
	 */
	AjaxModel saveMakeApiKey(AjaxModel model);

	/**
	 * 탈퇴승인 처리
	 * @param model
	 * @return
	 * @throws Exception
	 */
	AjaxModel saveUserDrop(AjaxModel model) throws Exception;

	/**
	 * ATTACH_ID 수정
	 * @param model
	 * @return
	 * @throws Exception
	 */
	AjaxModel saveUserAttachFileId(AjaxModel model) throws Exception;

	/**
	 * 로그인시 로그인 정보 수정
	 * @param userSessionModel
	 */
	void updateUserLoginInfo(UserSessionModel userSessionModel);

	/**
	 * 사용자 삭제
	 * @param model
	 * @return
	 */
    AjaxModel deleteUser(AjaxModel model);
}