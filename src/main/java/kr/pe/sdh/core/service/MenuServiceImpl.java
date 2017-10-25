package kr.pe.sdh.core.service;

import kr.pe.sdh.core.dao.CommonDAO;
import kr.pe.sdh.core.model.AjaxModel;
import kr.pe.sdh.core.model.MenuModel;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 시스템관리 > 메뉴관리 구현 클래스
 * 메뉴 관련 기능
 * @author 성동훈
 * @since 2017-01-05
 * @version 1.0
 * @see MenuService
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
@Service(value = "menuService")
public class MenuServiceImpl implements MenuService {

    @Resource(name = "commonDAO")
    private CommonDAO commonDAO;

    /**
     * {@inheritDoc}
     * @param authCd
     * @param sessionDiv
     * @return
     * @throws Exception
     */
    @Override
    public List<MenuModel> selectUsrMenuList(String authCd, String sessionDiv) throws Exception {
        Map<String, String> param = new HashMap<>();
        param.put("AUTH_CD" , authCd);
        param.put("MENU_DIV", sessionDiv);
        return commonDAO.list("menu.selectUsrMenuList", param);
    }

    /**
     * {@inheritDoc}
     * @param model
     * @return
     */
    @Override
    public AjaxModel selectMenuList(AjaxModel model) {
        Map<String, Object> params = model.getData();
        if(model.getUserSessionModel() == null){
            params.put("authCd", "DEFAULT");
        }else{
            params.put("authCd", model.getUserSessionModel().getAuthCd());
        }


        model.setDataList(commonDAO.list("menu.selectMenuList", params));
        return model;
    }

    /**
     * {@inheritDoc}
     * @param model
     * @return
     */
    @Override
    public AjaxModel saveMenu(AjaxModel model) {
        Map<String, Object> param = model.getData();

        String saveMode = (String)param.get("SAVE_MODE");

        param.put("REG_ID", model.getUserSessionModel().getUserId());
        param.put("MOD_ID", model.getUserSessionModel().getUserId());

        String newMenuId = (String)commonDAO.select("menu.selectNewMenuId", param);
        param.put("NEW_MENU_ID", newMenuId);
        // 신규저장일경우
        if("I".equals(saveMode)){

            int cnt = Integer.parseInt(String.valueOf(commonDAO.select("menu.selectCmmMenuCount", param)));
            if(cnt > 0){
                model.setCode("W00000012"); // 이미 존재하는 아이디 입니다.
                return model;
            }

            commonDAO.insert("menu.insertCmmMenu", param);


        }else{ // 수정일 경우
            String orgPmenu = (String)param.get("ORG_PMENU_ID");
            String pmenu    = (String)param.get("PMENU_ID");
            if(!orgPmenu.equals(pmenu)){
                param.put("IS_PMENU_DIFF", "T");
            }else{
                param.put("IS_PMENU_DIFF", "F");
            }

            commonDAO.update("menu.updateCmmMenu", param);

        }

        model.setCode("I00000003"); //저장되었습니다.

        return model;
    }

    /**
     * {@inheritDoc}
     * @param model
     * @return
     */
    @Override
    public AjaxModel deleteMenu(AjaxModel model) {
        Map<String, Object> param = model.getData();
        commonDAO.delete("menu.deleteCmmMenuBtnAll", param);
        commonDAO.delete("menu.deleteCmmMenuBtnAuthAll", param);
        commonDAO.delete("menu.deleteCmmMenuAuth", param);
        commonDAO.delete("menu.deleteCmmMenuTree", param);

        model.setCode("I00000004"); //삭제되었습니다.

        return model;
    }

    /**
     * {@inheritDoc}
     * @param model
     * @return
     */
    @Override
    public AjaxModel saveMenuBtn(AjaxModel model) {
        Map<String, Object> param = model.getData();

        String saveMode = (String)param.get("BTN_SAVE_MODE");

        param.put("REG_ID", model.getUserSessionModel().getUserId());
        param.put("MOD_ID", model.getUserSessionModel().getUserId());

        // 신규저장일경우
        if("I".equals(saveMode)){

            int cnt = Integer.parseInt(String.valueOf(commonDAO.select("menu.selectCmmMenuBtnCount", param)));
            if(cnt > 0){
                model.setCode("W00000012"); // 이미 존재하는 아이디 입니다.
                return model;
            }

            commonDAO.insert("menu.insertCmmMenuBtn", param);


        }else{ // 수정일 경우
            commonDAO.insert("menu.updateCmmMenuBtn", param);
        }

        model.setCode("I00000003"); //저장되었습니다.

        return model;
    }

    /**
     * {@inheritDoc}
     * @param model
     * @return
     */
    @Override
    public AjaxModel deleteMenuBtn(AjaxModel model) {
        List<Map<String, Object>> dataList = model.getDataList();

        if(dataList == null){
            dataList = new ArrayList<>();
        }

        if(model.getDataList() == null && model.getData() != null){
            dataList.add(model.getData());
        }

        for(Map<String, Object> param : dataList){
            commonDAO.delete("menu.deleteCmmMenuBtnAuth", param); // 메뉴별 버튼권한 삭제
            commonDAO.delete("menu.deleteCmmMenuBtn", param);     // 메뉴별 버튼 삭제
        }

        model.setCode("I00000004"); //삭제되었습니다.

        return model;
    }


    /**
     * {@inheritDoc}
     * @param menuModels
     * @param menuId
     * @return
     */
    @Override
    public MenuModel findRootMenu(List<MenuModel> menuModels, String menuId){
        for(MenuModel menuModel : menuModels){
            if(menuModel.getMenuId().equals(menuId)){
                if(menuModel.getMenuLevel().equals("1")){
                    return menuModel;
                }else{
                    return findRootMenu(menuModels, menuModel.getPmenuId());
                }
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     * @param menuModels
     * @param jsp
     * @return
     */
    @Override
    public MenuModel findJspMenu(List<MenuModel> menuModels, String jsp) {
        if(StringUtils.isEmpty(jsp)){
            return null;
        }

        for(MenuModel menuModel : menuModels){
            String menuPath = menuModel.getMenuPath();
            String menuUrl  = menuModel.getMenuUrl();
            if(StringUtils.isEmpty(menuPath) || StringUtils.isEmpty(menuUrl)){
                continue;
            }

            if(jsp.equals(menuPath+"/"+menuUrl)){
                return menuModel;
            }
        }

        return new MenuModel();
    }
}
