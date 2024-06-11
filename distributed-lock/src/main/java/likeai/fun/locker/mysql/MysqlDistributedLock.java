package likeai.fun.locker.mysql;

import likeai.fun.locker.DistributedLock;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author likeai
 */
public class MysqlDistributedLock implements DistributedLock {
    // TODO. How to inject dao? JdbcTemplate?
    protected final JdbcTemplate jdbcTemplate;

    public MysqlDistributedLock(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean lock() {
        return false;
    }

    @Override
    public boolean release() {
        return false;
    }

}
