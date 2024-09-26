package likeai.fun.plugins;

import static likeai.fun.StringUtils.hasText;

import com.mysql.cj.MysqlConnection;
import com.mysql.cj.Query;
import com.mysql.cj.interceptors.QueryInterceptor;
import com.mysql.cj.log.Log;
import com.mysql.cj.protocol.Resultset;
import com.mysql.cj.protocol.ServerSession;
import java.util.Properties;
import java.util.function.Supplier;

/**
 * @author likeai
 */
public class JdbcHintSqlWrapper implements QueryInterceptor {
  private final Supplier<String> comment;

  public JdbcHintSqlWrapper() {
    this.comment = () -> "jdbc-hints-wrapper";
  }

  @Override
  public QueryInterceptor init(MysqlConnection conn, Properties props, Log log) {
    return this;
  }

  @Override
  public <T extends Resultset> T preProcess(Supplier<String> sqlProvider, Query interceptedQuery) {
    String hits = this.comment.get();
    if (!hasText(hits)) {
      return null;
    }
    String sql = sqlProvider.get();
    if (!hasText(sql)) {
      return null;
    }
    sql = "/* " + hits + " */" + sql;
    // new LazyString()
    // ??
    return null;
  }

  @Override
  public boolean executeTopLevelOnly() {
    return false;
  }

  @Override
  public void destroy() {

  }

  @Override
  public <T extends Resultset> T postProcess(Supplier<String> sql, Query interceptedQuery, T originalResultSet, ServerSession serverSession) {
    return null;
  }
}
