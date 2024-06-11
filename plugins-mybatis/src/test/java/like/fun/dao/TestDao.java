package like.fun.dao;

import like.fun.BaseDao;
import org.apache.ibatis.annotations.Param;

public interface TestDao extends BaseDao {
    void test(@Param("key") String key);

    Long get(@Param("key") String key);
}
