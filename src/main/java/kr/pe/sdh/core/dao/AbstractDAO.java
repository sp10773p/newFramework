package kr.pe.sdh.core.dao;

import kr.pe.sdh.core.base.Constant;
import org.apache.ibatis.exceptions.ExceptionFactory;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.defaults.DefaultSqlSession;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.managed.ManagedTransactionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mybatis sqlSession을 구현한 추상클래스
 * 모든 DAO는 AbstractDAO를 상속하여 구현하여야 한다.
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
public class AbstractDAO extends SqlSessionDaoSupport{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private String RESULT_TYPE_LIST = "RESULT_TYPE_LIST";
    private String RESULT_TYPE_MAP = "RESULT_TYPE_MAP";

    /**
     * Insert 쿼리 수행
     * @param queryId
     * @param params
     * @return
     */
    public int insert(String queryId, Object params){
        printQueryId(queryId);
        return getSqlSession().insert(queryId, params);
    }

    /**
     * Update 쿼리 수행
     * @param queryId
     * @param params
     * @return
     */
    public int update(String queryId, Object params){
        printQueryId(queryId);
        return getSqlSession().update(queryId, params);
    }

    /**
     * Delete 쿼리 수행
     * @param queryId
     * @param params
     * @return
     */
    public int delete(String queryId, Object params){
        printQueryId(queryId);
        return getSqlSession().delete(queryId, params);
    }

    /**
     * 단일행 조회 쿼리 수행
     * @param queryId
     * @return
     */
    public Object select(String queryId){
        printQueryId(queryId);
        return getSqlSession().selectOne(queryId);
    }

    /**
     * 단일행 조회 쿼리 수행
     * @param queryId
     * @param params
     * @return
     */
    public Object select(String queryId, Object params){
        printQueryId(queryId);
        return getSqlSession().selectOne(queryId, params);
    }

    /**
     * 리스트 조회 쿼리 수행
     * @param queryId
     * @param <E>
     * @return
     */
    public <E> List<E> list(String queryId){
        printQueryId(queryId);
        return getSqlSession().selectList(queryId);
    }

    /**
     * 리스트 조회 쿼리 수행
     * @param queryId
     * @param params
     * @param <E>
     * @return
     */
    public <E> List<E> list(String queryId, Object params){
        printQueryId(queryId);
        return getSqlSession().selectList(queryId,params);
    }


    /**
     * 단일행 조회 쿼리 수행
     * - 컬럼값이 Null 이어도 컬럼값을 put 해준다
     * @param queryId
     * @return
     */
    public Object selectWithCol(String queryId){
        printQueryId(queryId);
        return selectWithCol(queryId, new HashMap());
    }

    /**
     * 단일행 조회 쿼리 수행
     *  - 컬럼값이 Null 이어도 컬럼값을 put 해준다
     * @param queryId
     * @param params
     * @return
     */
    public Object selectWithCol(String queryId, Map<String, Object> params){
        printQueryId(queryId);
        params.put(Constant.RESULTSET_METADATA_KEY.getCode(), Constant.RESULTSET_METADATA_KEY.getCode());
        Object ret = selectWithMetadata(queryId, params);
        if(ret != null && ret instanceof Map){
            for(String str : (List<String>)params.get(Constant.RESULTSET_METADATA_KEY.getCode())){
                if(!((Map)ret).containsKey(str)){
                    ((Map)ret).put(str, "");
                }
            }
        }
        return ret;
    }

    /**
     * 리스트 조회 쿼리 수행
     * 조회결과의 컬럼 헤더 정보를 params 인자에 추가
     * @param queryId
     * @param params
     * @param <E>
     * @return
     */
    public <E> List<E> listWithMetadata(String queryId, Object params){
        printQueryId(queryId);
        return (List<E>)executorWithMeta(RESULT_TYPE_LIST, queryId, params);
    }

    /**
     * 단일행 조회 쿼리 수행
     * 조회결과의 컬럼 헤더 정보를 params 인자에 추가
     * @param queryId
     * @param params
     * @return
     */
    public Object selectWithMetadata(String queryId, Object params){
        printQueryId(queryId);
        return executorWithMeta(RESULT_TYPE_MAP, queryId, params);
    }

    /**
     * 조회결과의 컬럼 헤더 정보를 params 인자에 추가
     * @param type
     * @param queryId
     * @param params
     * @return
     */
    private Object executorWithMeta(String type, String queryId, Object params){
        Object retobject = null;

        SqlSession sqlSession = getSqlSession();
        Configuration configuration = sqlSession.getConfiguration();

        Transaction tx = null;
        try {
            final Environment environment = configuration.getEnvironment();
            final TransactionFactory transactionFactory = getTransactionFactoryFromEnvironment(environment);
            tx = transactionFactory.newTransaction(environment.getDataSource(), null, false);
            //final SimpleExecutor executor = (SimpleExecutor) configuration.newExecutor(tx, ExecutorType.SIMPLE, autoCommit);
            ResultSetExecutor executor = new ResultSetExecutor(configuration, tx);
            DefaultSqlSession defaultSqlSession = new DefaultSqlSession(configuration, executor);

            if(RESULT_TYPE_LIST.equals(type)){
                retobject = defaultSqlSession.selectList(queryId, params);

            }else if(RESULT_TYPE_MAP.equals(type)){
                retobject = defaultSqlSession.selectOne(queryId, params);
            }

        } catch (Exception e) {
            closeTransaction(tx); // may have fetched a connection so lets call close()
            throw ExceptionFactory.wrapException("Error opening session.  Cause: " + e, e);
        } finally {
            ErrorContext.instance().reset();
        }

        return retobject;
    }

    /**
     * 쿼리 ID를 logger에 기록
     * @param queryId
     */
    private void printQueryId(String queryId) {
        if(logger.isDebugEnabled()){
            logger.debug("\t QueryId  \t:  " + queryId);
        }
    }

    private void closeTransaction(Transaction tx) {
        if (tx != null) {
            try {
                tx.close();
            } catch (SQLException ignore) {
                // Intentionally ignore. Prefer previous error.
            }
        }
    }

    private TransactionFactory getTransactionFactoryFromEnvironment(Environment environment) {
        if (environment == null || environment.getTransactionFactory() == null) {
            return new ManagedTransactionFactory();
        }
        return environment.getTransactionFactory();
    }

}
