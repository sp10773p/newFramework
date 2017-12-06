package kr.pe.sdh.core.dao;

import kr.pe.sdh.core.base.Constant;
import kr.pe.sdh.core.model.AjaxModel;
import kr.pe.sdh.common.model.UserSessionModel;
import kr.pe.sdh.common.service.CommonService;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Properties;

/**
 * Mybatis Interceptor
 * - 쿼리 수행시 파라미터 객체에 UserSessionModel을 추가
 * @author 김진호
 * @since 2017-01-20
 * @version 1.0
 * @see
 *
 * <pre>
 * == 개정이력(Modification Information) ==
 *
 * 수정일      수정자  수정내용
 * ----------- ------- ---------------------------
 * 2017.01.20  김진호  최초 생성
 *
 * </pre>
 */
@Intercepts({ @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }),
	@Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class }) })
@SuppressWarnings("rawtypes")
public class AddCommToParamInterceptor implements Interceptor {
	static int PARAMETER_INDEX = 1;
	
	@SuppressWarnings("unchecked")
	public Object intercept(final Invocation invocation) throws Throwable {
		final Object[] queryArgs = invocation.getArgs();
		final Object parameter = queryArgs[PARAMETER_INDEX];
		
		if(RequestContextHolder.getRequestAttributes() == null) {
			return invocation.proceed();
		}
		
		if(parameter instanceof AjaxModel || parameter instanceof Map) {
			ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();

			HttpServletRequest request = attr.getRequest();
			CommonService commonService = new CommonService();
		    UserSessionModel usrSessionMode = commonService.getUesrSessionModel(request);

			String sessionDiv = request.getHeader("sessiondiv");
			if(sessionDiv == null) {
				sessionDiv = request.getParameter("MENU_DIV");
			}

			String sessionKey = Constant.SESSION_KEY_USR.getCode();
			if(Constant.ADM_SESSION_DIV.getCode().equals(sessionDiv)){
				sessionKey = Constant.SESSION_KEY_ADM.getCode();
			}

		    if(usrSessionMode != null) {
			    if(parameter instanceof AjaxModel) {
			    	((AjaxModel)parameter).getData().put(sessionKey, usrSessionMode);
			    } else {
			    	((Map)parameter).put(sessionKey, usrSessionMode);
			    }
		    }
		}
		
		return invocation.proceed();
	}

	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	public void setProperties(Properties properties) {
	}
}
