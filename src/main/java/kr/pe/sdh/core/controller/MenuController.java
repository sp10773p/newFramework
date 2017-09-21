package kr.pe.sdh.core.controller;

import kr.pe.sdh.core.model.AjaxModel;
import kr.pe.sdh.core.service.MenuService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
public class MenuController {
    @Resource(name = "menuService")
    private MenuService menuService;

    @RequestMapping(value="jspView")
    public ModelAndView jspView(@RequestParam(value = "jsp") String jsp, HttpServletRequest request) throws Exception{
        ModelAndView mnv = new ModelAndView();
        mnv.setViewName(jsp);
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
