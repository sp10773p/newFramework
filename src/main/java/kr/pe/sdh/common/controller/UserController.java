package kr.pe.sdh.common.controller;

import kr.pe.sdh.common.model.UserSessionModel;
import kr.pe.sdh.common.service.CommonService;
import kr.pe.sdh.common.service.UserService;
import kr.pe.sdh.core.base.Constant;
import kr.pe.sdh.core.model.AjaxModel;
import kr.pe.sdh.core.util.RandomString;
import kr.pe.sdh.core.util.Sha256;
import kr.pe.sdh.core.util.WebUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * 시스템관리 > 사용자관리 Controller
 * @author 성동훈
 * @since 2017-01-05
 * @version 1.0
 * @see UserService
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
public class UserController {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource(name = "userService")
    UserService userService;

    @Resource(name = "commonService")
    CommonService commonService;


    private final LinkedHashMap<Long, String> webAccessKey = new LinkedHashMap<>();

    /**
     * 사용자 리스트 조회
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/user/selectUserList")
    @ResponseBody
    public AjaxModel selectUsrList(@RequestBody AjaxModel model) throws Exception{
        return userService.selectUsrList(model);
    }

    /**
     * 사용자 조회
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/user/selectUser")
    @ResponseBody
    public AjaxModel selectUsr(@RequestBody AjaxModel model) throws Exception{
        return userService.selectUsr(model);
    }

    /**
     * 사용자 저장
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/user/saveUser")
    @ResponseBody
    public AjaxModel saveUser(@RequestBody AjaxModel model) throws Exception {
        return userService.saveUser(model);
    }

    /**
     * 비밀번호 초기화
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/user/initPass")
    @ResponseBody
    public AjaxModel saveInitPass(@RequestBody AjaxModel model) throws Exception {
        return userService.saveInitPass(model);
    }

    /**
     * 사용자 승인처리
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/user/saveUserApprove")
    @ResponseBody
    public AjaxModel saveUserApprove(@RequestBody AjaxModel model) throws Exception {
        return userService.saveUserApprove(model);
    }

    /**
     * 탈퇴승인 처리
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/user/userDrop")
    @ResponseBody
    public AjaxModel userDrop(@RequestBody AjaxModel model) throws Exception {
        return userService.saveUserDrop(model);
    }


    /**
     * 사용자 삭제
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/user/deleteUser")
    @ResponseBody
    public AjaxModel deleteUser(@RequestBody AjaxModel model) {
        return userService.deleteUser(model);
    }

    /**
     * 사용자 WEB 접속 인증키 생성
     * @param request
     * @param userId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/user/createWebAccessKey")
    @ResponseBody
    public AjaxModel createWebAccessKey(HttpServletRequest request, @RequestParam("userId") String userId) {
        AjaxModel model = new AjaxModel();

        StringBuilder sb = new StringBuilder();
        HttpSession session = request.getSession(false);
        sb.append(userId).append(session.getId()).append(RandomString.random(10));

        String key = Sha256.encrypt(sb.toString());

        addWebAccessKey(key);

        Map<String, Object> retMap = new HashMap<>();
        retMap.put("accessKey", key);
        model.setData(retMap);

        return model;
    }

    /**
     * 사용자Web 접속
     * @param request
     * @throws Exception
     */
    @RequestMapping(value = "/user/userWebSite")
    public ModelAndView userWebSite(HttpServletRequest request,
                                        @RequestParam(value = "userId", required = false) String userId,
                                        @RequestParam(value = "key", required = false) String key) throws Exception {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("redirect:/");

        HttpSession session = request.getSession(true);

        // Session Validation
        if(isValidWebAccessKey(key)){
            UserSessionModel usrSessionModel = userService.selectUserSessionInfo(userId);

            String ip = WebUtil.getClientIp(request);
            usrSessionModel.setUserIp(ip);

            session.setAttribute(Constant.SESSION_KEY_USR.getCode(), usrSessionModel);

        }else{
            session.setAttribute(Constant.SESSION_KEY_USR.getCode(), null);

        }

        return mav;
    }

    /**
     * 사용자 WEB 접속 인증키관리 추가
     * @param key
     */
    private synchronized void addWebAccessKey(String key){
        synchronized (webAccessKey){
            webAccessKey.put(System.currentTimeMillis(), key);
        }
    }

    /**
     * 사용자 WEB 접속 인증키 검증
     * @param accessKey
     * @return
     * @throws Exception
     */
    private synchronized boolean isValidWebAccessKey(String accessKey) {
        boolean bool = false;
        synchronized (webAccessKey){
            List<Long> removeKey = new ArrayList<>();
            for ( Map.Entry entry : webAccessKey.entrySet() ) {
                long addTime = (long) entry.getKey();
                if((System.currentTimeMillis() - addTime) > (1000 * 5)){
                    removeKey.add(addTime);

                }else{
                    if(accessKey.equals(entry.getValue())){
                        removeKey.add(addTime);
                        bool = true;
                    }
                }
            }

            for(Long key : removeKey){
                webAccessKey.remove(key);
            }
        }

        return bool;
    }
}
