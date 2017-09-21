package kr.pe.sdh.core.tld;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

/**
 * decimalFormat taglib
 * @author 김진호
 * @since 2017-01-05
 * @version 1.0
 * @see
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
public class DecimalFormatTagHandler extends SimpleTagSupport {
    private String value = "true";

    @Override
    public void doTag() throws JspException, IOException {
        getJspContext().getOut().write(" decimalFormat='" + this.value + "' ");
    }

    public void setValue(String value) {
        this.value = value;
    }
}
