package likeai.fun.plugins;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * hint
 *
 * @author likeai
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class ExecutorHintSqlPlugin implements Interceptor {
    private final Supplier<String> comment;

    public ExecutorHintSqlPlugin(Supplier<String> comment) {
        this.comment = comment;
    }

    @Override
    public Object intercept(Invocation invocation) throws InvocationTargetException, IllegalAccessException {
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];
        Object parameter = args[1];
        BoundSql bound = mappedStatement.getBoundSql(parameter);
        bound = new BoundSql(mappedStatement.getConfiguration(), String.format("/* %s */ %s", this.comment.get(), bound.getSql()), bound.getParameterMappings(), bound.getParameterObject());
        args[0] = copyMappedStatement(mappedStatement, new BoundSqlSqlSource(bound));
        return invocation.proceed();
    }

    private MappedStatement copyMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        builder.keyProperty(arrayToCommaDelimitedString(ms.getKeyProperties()));
        builder.keyColumn(arrayToCommaDelimitedString(ms.getKeyColumns()));
        builder.databaseId(ms.getDatabaseId());
        builder.lang(ms.getLang());
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.cache(ms.getCache());
        builder.useCache(ms.isUseCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        return builder.build();
    }

    public static String arrayToCommaDelimitedString(Object[] array) {
        if (array == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(array[i]);
        }
        return sb.toString();
    }

    private static class BoundSqlSqlSource implements SqlSource {
        private final BoundSql boundSql;

        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        @Override
        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }

}
