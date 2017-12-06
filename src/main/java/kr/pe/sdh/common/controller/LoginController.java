package kr.pe.sdh.common.controller;

import kr.pe.sdh.common.service.UserService;
import kr.pe.sdh.core.base.Constant;
import kr.pe.sdh.core.model.AccessLogModel;
import kr.pe.sdh.common.model.UserSessionModel;
import kr.pe.sdh.common.service.CommonService;
import kr.pe.sdh.common.service.MenuService;
import kr.pe.sdh.core.util.DateUtil;
import kr.pe.sdh.core.util.KeyGen;
import kr.pe.sdh.core.util.Sha256;
import kr.pe.sdh.core.util.WebUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 로그인, 로그아웃 처리 Controller
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
public class LoginController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "commonService")
	private CommonService commonService;

	@Resource(name = "menuService")
	private MenuService menuService;

	/**
	 * 로그인 처리
	 * @param userId
	 * @param userPw
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/loginAction")
	public ModelAndView loginAction(@RequestParam(value = "userId") String userId, @RequestParam(value = "userPw") String userPw, HttpServletRequest request) throws Exception {

		UserSessionModel userSessionModel = userService.selectUserSessionInfo(userId);

		ModelAndView mnv = new ModelAndView();
		AccessLogModel accessLogModel = new AccessLogModel();

		String ip  = WebUtil.getClientIp(request);
		String msg = null;

		mnv.setViewName("main/main");

		/* TODO 유저화면이 생기면 그때 변경
		if(Constant.ADM_SESSION_DIV.getCode().equals(sessionDiv)){
			redirect = "main/main";

		}*/

		if (userSessionModel == null) {
			msg = commonService.getMessage("I00000001");// 아이디 또는 비밀번호가 올바르지 않습니다.

			//} else if (this.isPasswordEncrypt &&  !Sha256.compareEncrypt(userSessionModel.getUserPw(), userPw) ||
		} else if (!userSessionModel.getUserPw().equals(Sha256.encrypt(userPw))) {
			msg = commonService.getMessage("I00000001"); // 아이디 또는 비밀번호가 올바르지 않습니다.

		}else if(!"1".equals(userSessionModel.getUserStatus()) || !"Y".equals(userSessionModel.getUseChk())){ // 사용자 상태( 1 : 가입승인 ) || 사용여부
			msg = commonService.getMessage("W00000062"); // 사용할 수 없는 상태 입니다.

		}else if(userSessionModel.getMenuModelList().size() == 0){
			msg = commonService.getMessage("W00000022"); // 사용할 수 있는 메뉴가 없습니다. \n권한이나 접속한 사이트를 확인 하세요.

			// IP접속허용체크
		}/*else if("M".equals(sessionDiv) && isAdminIpCheck && !checkIpMatching(userId, ip)){
			msg = commonService.getMessage("W00000038"); // IP 접속권한이 없습니다.

		}*/

		if(msg == null) {
			userSessionModel.setLoginError(0);
			if (userSessionModel.getLoginLast() == null || DateUtil.parse(userSessionModel.getLoginLast().replaceAll("-", "").substring(0, 8), "yyyyMMdd").before(DateUtil.parse("20000101", "yyyyMMdd"))) {
				userSessionModel.setLoginStart(DateUtil.stamp().toString());
			}

			userSessionModel.setLoginLast(DateUtil.stamp().toString());
			userSessionModel.setModId(userSessionModel.getUserId());

			// 사용자 로그인 정보 갱신
			userService.updateUserLoginInfo(userSessionModel);

			userSessionModel.setUserIp(ip);
			HttpSession session = request.getSession(true);
			session.setAttribute(Constant.SESSION_KEY_USR.getCode(), userSessionModel);

			accessLogModel.setRmk("로그인 성공");
		}else{
			mnv.addObject("userId" , userId);
			mnv.addObject("userPw" , userPw);
			mnv.addObject("msg"    , msg);

			mnv.setViewName("login/login");

			accessLogModel.setRmk("로그인 실패[" + msg + "]");
		}

		accessLogModel.setSid(KeyGen.getRandomTimeKey());
		accessLogModel.setSessionId(request.getSession().getId());
		accessLogModel.setLogDiv(Constant.USR_SESSION_DIV.getCode());
		accessLogModel.setLoginIp(ip);
		accessLogModel.setScreenId("LOGIN");
		accessLogModel.setScreenNm("로그인");
		accessLogModel.setUserId(userId);
		accessLogModel.setUri(request.getRequestURI());

		commonService.saveAccessLog(accessLogModel);

		return mnv;
	}

	/**
	 * 로그아웃 처리
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/logout")
	public ModelAndView logoutAction(@RequestParam(value = "sessionDiv") String sessionDiv, HttpServletRequest request, HttpServletResponse response) throws Exception  {
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();

		UserSessionModel sessionModel = commonService.getUesrSessionModel(request);

		if(sessionModel != null){
			AccessLogModel accessLogModel = new AccessLogModel();
			accessLogModel.setLogDiv(sessionDiv);
			accessLogModel.setSid(KeyGen.getRandomTimeKey());
			accessLogModel.setSessionId(session.getId());
			accessLogModel.setLoginIp(WebUtil.getClientIp(request));
			accessLogModel.setScreenId("LOGOUT");
			accessLogModel.setScreenNm("로그아웃");
			accessLogModel.setUserId(sessionModel.getUserId());
			accessLogModel.setUri(request.getRequestURI());

			commonService.saveAccessLog(accessLogModel);

			if(Constant.ADM_SESSION_DIV.getCode().equals(sessionDiv)) {
				mav.setViewName(String.format("redirect:%s", "/admin"));
				session.setAttribute(Constant.SESSION_KEY_ADM.getCode(), null);

			} else if(Constant.USR_SESSION_DIV.getCode().equals(sessionDiv)) {
					mav.setViewName(String.format("redirect:%s", "/"));
					session.setAttribute(Constant.SESSION_KEY_USR.getCode(), null);

			}
		}else{
			mav.setViewName(String.format("redirect:%s", "/"));

		}

		return mav;
	}


	/**
	 * check if IP address match pattern
	 *
	 * @param userId
	 * @param address
	 *            *.*.*.* , 192.168.1.0-255 , *
	 * @param address
	 *            - 192.168.1.1
	 *            	address = 10.2.88.12  pattern = *.*.*.*   result: true
	 *              address = 10.2.88.12  pattern = *   result: true
	 *              address = 10.2.88.12  pattern = 10.2.88.12-13   result: true
	 *              address = 10.2.88.12  pattern = 10.2.88.13-125   result: false
	 * @return true if address match pattern
	 */
	private boolean checkIpMatching(String userId, String address) {
		try {
			boolean result = true;
			List<String> ipList = commonService.selectUserIpAccess(userId);
			if(ipList.size() == 0){
				return true;
			}

			for (String pattern : ipList) {
				if (pattern.equals("*.*.*.*") || pattern.equals("*"))
					return true;

				result = true;
				String[] mask = pattern.split("\\.");
				String[] ip_address = address.split("\\.");
				for (int i = 0; i < mask.length; i++) {
					if (mask[i].equals("*") || mask[i].equals(ip_address[i])) {
					}
					else if (mask[i].contains("-")) {
						byte min = Byte.parseByte(mask[i].split("-")[0]);
						byte max = Byte.parseByte(mask[i].split("-")[1]);
						byte ip = Byte.parseByte(ip_address[i]);
						if (ip < min || ip > max)
							result = false;
					} else
						result = false;
				}

				if (result) return true;
			}
			return result;
		}catch(ArrayIndexOutOfBoundsException e){
			return address.equals("0:0:0:0:0:0:0:1");
		}
	}

	private void saveAccessLog(HttpServletRequest request, String logDiv, String screenNm){
		AccessLogModel accessLogModel = new AccessLogModel();

		accessLogModel.setSid(KeyGen.getRandomTimeKey());
		accessLogModel.setSessionId(request.getSession().getId());
		accessLogModel.setLogDiv(logDiv);
		accessLogModel.setLoginIp(WebUtil.getClientIp(request));
		accessLogModel.setScreenId("ACCESS");
		accessLogModel.setScreenNm(screenNm);
		accessLogModel.setUri(request.getRequestURI());

		commonService.saveAccessLog(accessLogModel);

	}
}
