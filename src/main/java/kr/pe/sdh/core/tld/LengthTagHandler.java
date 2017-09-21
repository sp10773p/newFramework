package kr.pe.sdh.core.tld;

import org.apache.commons.lang.StringUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

/**
 * length taglib
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
public class LengthTagHandler extends SimpleTagSupport {
    private String value = null;
    private String unicode = null;

    @Override
    public void doTag() throws JspException, IOException {
        String str = " length";
        if(StringUtils.isNotEmpty(value)){
            str += "='" + value + "'";
        }

        if(StringUtils.isNotEmpty(unicode)){
            str += " unicode='" + unicode + "' ";
        }
        str += " ";

        getJspContext().getOut().write(str);
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setUnicode(String unicode) {
        this.unicode = unicode;
    }
}
