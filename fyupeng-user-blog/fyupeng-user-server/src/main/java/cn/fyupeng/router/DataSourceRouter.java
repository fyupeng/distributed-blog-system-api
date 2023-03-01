package cn.fyupeng.router;

import cn.fyupeng.utils.DataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * AbstractRoutingDataSource: 实现 Aop 动态切换 的关键。
 * <br/>
 * 原理: 多数据源 key 设置 与 ThreadLocal key 设置 相同 来实现，
 * ThreadLocal key 当前线程 按照 事务方法 注解 DataSourceSwitcher 设置为 master/slave 来选择。
 *
 * @Auther: fyp
 * @Date: 2023/2/23
 * @Description: 数据源路由器
 * @Package: cn.fyupeng.router
 * @Version: 1.0
 */
@Slf4j
public class DataSourceRouter extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        String dataSourceName = DataSourceContextHolder.get();
        log.info("Currently selected data source: {} ", dataSourceName);
        return dataSourceName;
    }
}
