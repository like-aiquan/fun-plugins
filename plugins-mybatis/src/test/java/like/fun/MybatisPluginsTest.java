package like.fun;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import like.fun.dao.TestDao;
import likeai.fun.plugins.ConnectionHintSqlPlugin;
import likeai.fun.plugins.ExecutorHintSqlPlugin;
import likeai.fun.plugins.XmlParseHintSqlDriver;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author likeai
 */
public class MybatisPluginsTest {
    Configuration configuration;
    HikariDataSource dataSource;

    @BeforeEach
    void run() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/db1?useUnicode=true&characterEncoding=UTF-8&useLegacyDatetimeCode=false&serverTimezone=Asia/Shanghai&logger=com.mysql.cj.log.Slf4JLogger&profileSQL=true");
        dataSource.setUsername("root");
        dataSource.setPassword("1234");
        this.dataSource = dataSource;
        this.configuration = new Configuration();
        this.configuration.setEnvironment(new Environment("development", new JdbcTransactionFactory(), dataSource));

    }

    @Test
    void testInterceptor() {
        this.configuration.getLanguageRegistry().setDefaultDriverClass(XmlParseHintSqlDriver.class);

        this.configuration.addMappers("like.fun.dao", BaseDao.class);

        this.configuration.addInterceptor(new ConnectionHintSqlPlugin(() -> "ConnectionHintSqlPlugin"));
        this.configuration.addInterceptor(new ExecutorHintSqlPlugin(() -> "ExecutorHintSqlPlugin"));

        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(this.configuration);
        try (SqlSession session = factory.openSession()) {
            TestDao mapper = session.getMapper(TestDao.class);
            mapper.test("test");
            session.commit();
        }
    }

    @Test
    void testJDBCHits() {
        HikariDataSource dataSource = new HikariDataSource();
        String uri = "jdbc:mysql://localhost:3306/db1"
            + "?useUnicode=true"
            + "&characterEncoding=UTF-8"
            + "&useLegacyDatetimeCode=false"
            + "&serverTimezone=Asia/Shanghai"
            + "&logger=com.mysql.cj.log.Slf4JLogger"
            // 无法重写 sql, 夭折警告⚠️
            + "&queryInterceptors=likeai.fun.plugins.JdbcHintSqlWrapper"
            + "&profileSQL=true";
        dataSource.setJdbcUrl(uri);
        dataSource.setUsername("root");
        dataSource.setPassword("1234");
        try (Connection connection = dataSource.getConnection()){
            try (Statement stmt = connection.createStatement()) {
                String sql = "SELECT * FROM test;";
                // todo hikari cp or jdbc?
                ResultSet rs = stmt.executeQuery(sql);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
