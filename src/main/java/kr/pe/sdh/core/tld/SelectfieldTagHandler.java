package kr.pe.sdh.core.tld;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

/**
 * selectfield taglib
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
public class SelectfieldTagHandler extends SimpleTagSupport {
    private int viewType = 1;

    @Override
    public void doTag() throws JspException, IOException {
        getJspContext().getOut().write(" selectfield='" + this.viewType + "' ");
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }
}
