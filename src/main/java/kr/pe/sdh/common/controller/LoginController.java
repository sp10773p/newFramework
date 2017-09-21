package kr.pe.sdh.common.controller;

import kr.pe.sdh.common.service.UsrService;
import kr.pe.sdh.core.base.Constant;
import kr.pe.sdh.core.model.AccessLogModel;
import kr.pe.sdh.core.model.MenuModel;
import kr.pe.sdh.core.model.UsrSessionModel;
import kr.pe.sdh.core.service.CommonService;
import kr.pe.sdh.core.service.MenuService;
import kr.pe.sdh.core.util.DateUtil;
import kr.pe.sdh.core.util.KeyGen;
import kr.pe.sdh.core.util.Sha256;
import kr.pe.sdh.core.util.WebUtil;
import org.apache.commons.lang.StringUtils;
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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
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

	@Resource(name = "usrService")
	private UsrService usrService;

	@Resource(name = "commonService")
	private CommonService commonService;

	@Resource(name = "menuService")
	private MenuService menuService;

	/**
	 * 어드민 사이트 접속 화면 이동
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/adminLogin")
	public ModelAndView adminLogin(HttpServletRequest request) throws Exception {
		ModelAndView mnv = new ModelAndView();
		mnv.setViewName("login/adminLogin");

		HttpSession session = request.getSession();

		if(session.getAttribute(Constant.SESSION_KEY_ADM.getCode()) != null){
			request.setAttribute("sessionDiv", Constant.ADM_SESSION_DIV.getCode());
			mnv.setViewName(String.format("forward:%s", "main/adminMain"));
			return mnv;
		}

		saveAccessLog(request, Constant.ADM_SESSION_DIV.getCode(), "어드민사이트 접속");

		return mnv;
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
