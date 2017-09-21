package kr.pe.sdh.core.dao;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.MappedStatement.Builder;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Properties;

/**
 * 쿼리 수행시 쿼리에 페이징 쿼리를 추가
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
@Intercepts({ @Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class }) })
@SuppressWarnings("rawtypes")
public class GridPagingInterceptor implements Interceptor {
	static int MAPPED_STATEMENT_INDEX = 0;
	static int PARAMETER_INDEX = 1;
	
	static String PAGING_PRE = "\nSELECT AAA.* FROM(SELECT COUNT(*) OVER() AS TOTAL_COUNT, %s, AA.* FROM(\n\n\t";
	static String PAGING_POST = "\n\n) AA %s ) AAA WHERE AAA.RN BETWEEN %d AND %d ";
	
	public Object intercept(final Invocation invocation) throws Throwable {
		final Object[] queryArgs = invocation.getArgs();
		final MappedStatement ms = (MappedStatement) queryArgs[MAPPED_STATEMENT_INDEX];
		final Object parameter = queryArgs[PARAMETER_INDEX];

		final BoundSql boundSql = ms.getBoundSql(parameter);
        BoundSql newBoundSql = copyFromBoundSql(ms, boundSql, getPage(boundSql.getSql().trim(), parameter));

        MappedStatement newMs = copyFromMappedStatement(ms, new BoundSqlSqlSource(newBoundSql));

        queryArgs[MAPPED_STATEMENT_INDEX] = newMs;

		return invocation.proceed();
	}
	
	private String getPage(String org, Object parameter) {
		if(parameter instanceof Map) {
			if(!((Map)parameter).containsKey("PAGE_INDEX") && !((Map)parameter).containsKey("PAGE_ROW")) {
				return org;
			}
			
			if(StringUtils.isEmpty(((Map)parameter).get("PAGE_INDEX")) && StringUtils.isEmpty(((Map)parameter).get("PAGE_ROW"))) {
				return org;
			}

			String rnumStr = "";
			String sort = "";
			if(((Map)parameter).get("SORT_STR") != null){
				rnumStr = "ROW_NUMBER() OVER (ORDER BY " + ((Map)parameter).get("SORT_STR") + ") RN ";
				sort    = "ORDER BY " + ((Map)parameter).get("SORT_STR");
			}else{
				rnumStr = "AA.RNUM AS RN ";
			}
			
			return String.format(PAGING_PRE, rnumStr)  + org + String.format(PAGING_POST, sort, ((Map)parameter).get("START"), ((Map)parameter).get("END"));
		} else {
			return org;
		}
	}

	// see: MapperBuilderAssistant
	private MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
		Builder builder = new Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());

		builder.resource(ms.getResource());
		builder.fetchSize(ms.getFetchSize());
		builder.statementType(ms.getStatementType());
		builder.keyGenerator(ms.getKeyGenerator());
		if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
			StringBuilder keyProperties = new StringBuilder();
			for (String keyProperty : ms.getKeyProperties()) {
				keyProperties.append(keyProperty).append(",");
			}
			keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
			builder.keyProperty(keyProperties.toString());
		}

		// setStatementTimeout()
		builder.timeout(ms.getTimeout());

		// setStatementResultMap()
		builder.parameterMap(ms.getParameterMap());

		// setStatementResultMap()
		builder.resultMaps(ms.getResultMaps());
		builder.resultSetType(ms.getResultSetType());

		// setStatementCache()
		builder.cache(ms.getCache());
		builder.flushCacheRequired(ms.isFlushCacheRequired());
		builder.useCache(ms.isUseCache());

		return builder.build();
	}

	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	public void setProperties(Properties properties) {
	}
	
    public static BoundSql copyFromBoundSql(MappedStatement ms, BoundSql boundSql, String sql) {
		BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), sql, boundSql.getParameterMappings(), boundSql.getParameterObject());
		for (ParameterMapping mapping : boundSql.getParameterMappings()) {
			String prop = mapping.getProperty();
			if (boundSql.hasAdditionalParameter(prop)) {
				newBoundSql.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop));
			}
		}
		return newBoundSql;
	}
    
    public static class BoundSqlSqlSource implements SqlSource {
        BoundSql boundSql;

        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }
}
