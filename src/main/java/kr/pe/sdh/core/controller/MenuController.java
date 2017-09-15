package kr.pe.sdh.core.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class MenuController {

    @RequestMapping(value="jspView")
    public ModelAndView jspView(@RequestParam(value = "jsp") String jsp, HttpServletRequest request) throws Exception{
        ModelAndView mnv = new ModelAndView();
        mnv.setViewName(jsp);
        return mnv;
    }
}
