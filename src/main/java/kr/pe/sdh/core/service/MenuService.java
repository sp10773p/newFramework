package kr.pe.sdh.core.service;

import kr.pe.sdh.core.model.AjaxModel;
import kr.pe.sdh.core.model.MenuModel;

import java.util.List;

/**
 * 시스템관리 > 메뉴관리 추상 클래스
 * 메뉴 관련 기능
 * @author 성동훈
 * @since 2017-01-05
 * @version 1.0
 * @see MenuServiceImpl
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
public interface MenuService {
    /**
     * 사용자메뉴 조회
     * @param authCd
     * @param sessionDiv
     * @return
     * @throws Exception
     */
    public List<MenuModel> selectUsrMenuList(String authCd, String sessionDiv) throws Exception;

    /**
     * 메뉴리스트 조회
     * @param model
     * @return
     */
    AjaxModel selectMenuList(AjaxModel model);

    /**
     * 메뉴 저장
     * @param model
     * @return
     */
    AjaxModel saveMenu(AjaxModel model);


    /**
     * 메뉴삭제
     * @param model
     * @return
     */
    AjaxModel deleteMenu(AjaxModel model);

    /**
     * 메뉴별 버튼 저장
     * @param model
     * @return
     */
    AjaxModel saveMenuBtn(AjaxModel model);

    /**
     * 메뉴별 버튼 삭제
     * @param model
     * @return
     */
    AjaxModel deleteMenuBtn(AjaxModel model);

    /**
     * 해당 menuId의 최상위 menuId를 가지 MenuModel을 반환
     * @param menuModels
     * @param menuId
     * @return
     */
    MenuModel findRootMenu(List<MenuModel> menuModels, String menuId);

    /**
     * 해당 jsp에 해당하는 MenuModel을 반환
     * @param menuModels
     * @param jsp
     * @return
     */
    MenuModel findJspMenu(List<MenuModel> menuModels, String jsp);
}
