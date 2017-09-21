package kr.pe.sdh.core.exception;


import kr.pe.sdh.core.model.AjaxModel;

/**
 * 예외로 메세지 처리시 사용하는 Exception
 * @author 김진호
 * @since 2017-01-05
 * @version 1.0
 * @see CommonExceptionHandler
 *
 * <pre>
 * == 개정이력(Modification Information) ==
 *
 * 수정일      수정자  수정내용
 * ----------- ------- ---------------------------
 * 2017.01.05  김진호  최초 생성
 *
 * </pre>
 */
public class BizException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private AjaxModel model = null;

	public BizException(String msg) {
		super(msg);
	}
	
	public BizException(AjaxModel model) {
		super("[" + model.getCode() + "] " + model.getMsg());
		
		this.model = model;
	}
	
	public AjaxModel getModel() {
		return this.model;
	}
}
