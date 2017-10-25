package kr.pe.sdh.core.base;

/**
 * 상수 정의
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
public enum Constant {
    QUERY_KEY("qKey"), // query key
    DEFAULT_PAGE_ROW("10"), // 페이징 기본 로우 수
    SUFFIX_COUNT_STR("TotalCount"), // 카운트쿼리 접미사
    SUMMARY_QUERY_KEY("SUMMARY_QKEY"), // 합계/집계 쿼리 키
    SUMMARY_TITLE("합계"), // 합계/집계 필드 문구

    ACTION_NM("ACTION_NM"),               // Action NM
    ACTION_MENU_ID("ACTION_MENU_ID"),     // CURRENT ACTION MENU ID
    ACTION_MENU_NM("ACTION_MENU_NM"),     // CURRENT ACTION MENU NAME
    ACTION_MENU_DIV("ACTION_MENU_DIV"),   // CURRENT ACTION MENU 구분('W':사용자, 'M':어드민)
    ACTION_TOP_MENU_NM("ACTION_TOP_MENU_NM"),
    ACTION_TOP_MENU_ID("ACTION_TOP_MENU_ID"),

    RESULTSET_METADATA_KEY("RESULTSET_METADATA_KEY"), // ResultSetMetadata 셋팅 여부 키

    SESSION_KEY_USR("USR_SESSION"), // USER SESSION KEY
    SESSION_KEY_ADM("ADM_SESSION"), // ADMIN SESSION KEY

    USR_SESSION_DIV("W"), // 사용자
    ADM_SESSION_DIV("M"), // 어드민

    OPEN_API_DIV("A"), // Open API
    BATCH_DIV("B"), // BATCH

    POST_TYPE("POST_TYPE"),
    POST_INSERT("1"),
    POST_DELETE("2"),

    AJAX_MODEL("AJAX_MODEL"),
    SESSION_MENU_LIST("SESSION_MENU_LIST");


    private String key;
    Constant(String key){
        this.key = key;
    }

    public String getCode(){
        return this.key;
    }

}