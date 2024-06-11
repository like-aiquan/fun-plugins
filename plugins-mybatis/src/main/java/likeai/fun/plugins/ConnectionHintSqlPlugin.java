package likeai.fun.plugins;

import java.sql.Connection;
import java.util.function.Supplier;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class ConnectionHintSqlPlugin implements Interceptor {
    private final Supplier<String> comment;

    public ConnectionHintSqlPlugin(Supplier<String> comment) {
        this.comment = comment;
    }

    /**
     * I think it should be the minimum impact on the performance of the way.
     * Using the reflection will redefine content
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaObject = MetaObject.forObject(statementHandler, SystemMetaObject.DEFAULT_OBJECT_FACTORY, SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY, new DefaultReflectorFactory());
        String modifiedSql = "/* " + this.comment.get() + " */" + statementHandler.getBoundSql().getSql();
        metaObject.setValue("boundSql.sql", modifiedSql);
        return invocation.proceed();
    }
}
