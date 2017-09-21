package kr.pe.sdh.core.controller;

import kr.pe.sdh.core.base.Constant;
import kr.pe.sdh.core.model.AjaxModel;
import kr.pe.sdh.core.service.MenuService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class MenuController {
    @Resource(name = "menuService")
    private MenuService menuService;

    @RequestMapping(value="/jspView")
    public ModelAndView jspView(@RequestParam(value = "jsp") String jsp, HttpServletRequest request) throws Exception{
        ModelAndView mnv = new ModelAndView();
        mnv.setViewName(jsp);
        return mnv;
    }

    @RequestMapping(value="/admin")
    public ModelAndView admin(HttpServletRequest request) throws Exception{
        ModelAndView mnv = new ModelAndView();

        HttpSession session = request.getSession();

        if(session.getAttribute(Constant.SESSION_KEY_ADM.getCode()) == null){
            mnv.setViewName("/login/adminLogin");

        }else{
            request.setAttribute("sessionDiv", Constant.ADM_SESSION_DIV.getCode());
            mnv.setViewName("/main/adminMain");

        }

        return mnv;
    }

    /**
     * 메뉴 조회
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/menu/menuList")
    @ResponseBody
    public AjaxModel getMenuList(AjaxModel model)throws Exception{
        return menuService.selectMenuList(model);
    }

}
