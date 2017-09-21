package kr.pe.sdh.core.dao;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * ucpapp 계정의 DB 접속 DAO
 *  - META-INF/context.xml : jdbc/ecdpJNDI
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
@Repository
public class CommonDAO extends AbstractDAO {

    @Resource(name = "sqlSessionTemplate")
    @Override
    public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
        super.setSqlSessionTemplate(sqlSessionTemplate);
    }
}
