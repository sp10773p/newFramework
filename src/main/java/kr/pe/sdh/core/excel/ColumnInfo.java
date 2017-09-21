package kr.pe.sdh.core.excel;

/**
 * 그리드 엑셀 다운로드 컬럼 정보
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
 * 2017.01.05  김도형  최초 생성
 *
 * </pre>
 */
public class ColumnInfo {

    private String headerId;
    private String headerName;
    private String mandatoryYn;
    private int maxColumnSize;
    
    public String getHeaderId() {
        return headerId;
    }
    public void setHeaderId(String headerId) {
        this.headerId = headerId;
    }
    public String getHeaderName() {
        return headerName;
    }
    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }
    public String getMandatoryYn() {
        return mandatoryYn;
    }
    public void setMandatoryYn(String mandatoryYn) {
        this.mandatoryYn = mandatoryYn;
    }
    public int getMaxColumnSize() {
        return maxColumnSize;
    }
    public void setMaxColumnSize(int maxColumnSize) {
        this.maxColumnSize = maxColumnSize;
    }
    
}
