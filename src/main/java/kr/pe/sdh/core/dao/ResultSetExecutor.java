package kr.pe.sdh.core.dao;

import kr.pe.sdh.core.base.Constant;
import org.apache.ibatis.executor.SimpleExecutor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 쿼리 수행 결과 처리시 파라미터에 쿼리 결과 컬럼 정보를 추가
 * @author 성동훈
 * @since 2017-01-05
 * @version 1.0
 * @see AbstractDAO
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
public class ResultSetExecutor extends SimpleExecutor {
    public ResultSetExecutor(Configuration configuration, Transaction transaction) {
        super(configuration, transaction);
    }

    @Override
    public <E> List<E> doQuery(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        Statement stmt = null;
        try {
            Configuration configuration = ms.getConfiguration();
            StatementHandler handler = configuration.newStatementHandler(this, ms, parameter, rowBounds, resultHandler, boundSql);
            stmt = prepareStatement(handler, ms.getStatementLog());

            List<E> result =  handler.query(stmt, resultHandler);

            if(parameter instanceof Map && ((Map)parameter).containsKey(Constant.RESULTSET_METADATA_KEY.getCode())){
                String resultsetMetadataKey = (String)((Map)parameter).get(Constant.RESULTSET_METADATA_KEY.getCode());
                ResultSet rs = stmt.getResultSet();
                ResultSetMetaData metadata = rs.getMetaData();
                int columnCount = metadata.getColumnCount();
                List<String> columns = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metadata.getColumnName(i);
                    columns.add(columnName);
                }

                ((Map)parameter).put(resultsetMetadataKey, columns);
            }

            return result;
        } finally {
            closeStatement(stmt);
        }
    }

    private Statement prepareStatement(StatementHandler handler, Log statementLog) throws SQLException {
        Statement stmt;
        Connection connection = getConnection(statementLog);
        stmt = handler.prepare(connection);
        handler.parameterize(stmt);
        return stmt;
    }
}
