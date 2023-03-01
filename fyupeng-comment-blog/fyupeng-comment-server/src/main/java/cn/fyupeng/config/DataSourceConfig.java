package cn.fyupeng.config;

import cn.fyupeng.enums.DataSourceEnum;
import cn.fyupeng.router.DataSourceRouter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.servlet.Filter;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: fyp
 * @Date: 2023/2/23
 * @Description: 数据源配置
 * @Package: cn.fyupeng.config
 * @Version: 1.0
 */
@Slf4j
@Configuration
public class DataSourceConfig {

    @Value("${druid.stat.loginUsername}")
    private String loginUsername;

    @Value("${druid.stat.loginPassword}")
    private String loginPassword;

    @Value("${druid.stat.allow}")
    private String allow;

    @Value("${druid.stat.deny}")
    private String deny;

    @Value("${druid.stat.resetEnable}")
    private String resetEnable;

    @Value("${druid.filter.exclusions}")
    private String exclusions;

    @Value("${druid.filter.urlPatterns}")
    private String urlPatterns;

    @Bean(name = "dbMaster")
    @ConfigurationProperties(prefix = "spring.datasource.master")
    public DataSource dbMaster() {
        return new DruidDataSource();
    }

    @Bean(name = "dbSlave")
    @ConfigurationProperties(prefix = "spring.datasource.slave")
    public DataSource dbSlave() {
        return new DruidDataSource();
    }


    @Primary
    @Bean(name = "dataSourceRouter")
    public DataSource dataSourceRouter(@Qualifier("dbMaster") DataSource master, @Qualifier("dbSlave") DataSource slave) {
        DataSourceRouter dataSourceRouter = new DataSourceRouter();
        DruidDataSource ddsMaster = (DruidDataSource) master;
        DruidDataSource ddsSlave = (DruidDataSource) slave;
        log.info("----------- Read master-slave replication configuration information BEGIN -----------" );
        log.info("master:" );
        log.info("Initialize number of connections: {}", ddsMaster.getInitialSize());
        log.info("Minimum number of idle connections: {}", ddsMaster.getMinIdle());
        log.info("Maximum number of connections: {}", ddsMaster.getMaxActive());
        log.info("Whether to turn on detection when applying for a connection: {}", ddsMaster.isTestOnBorrow());
        log.info("slave:" );
        log.info("Initialize number of connections: {}", ddsSlave.getInitialSize());
        log.info("Minimum number of idle connections: {}", ddsSlave.getMinIdle());
        log.info("Maximum number of connections: {}", ddsSlave.getMaxActive());
        log.info("Whether to turn on detection when applying for a connection: {}", ddsSlave.isTestOnBorrow());
        log.info("----------- Read master-slave replication configuration information END -----------" );

        Map<Object, Object> multi = new HashMap<>(5);
        multi.put(DataSourceEnum.MASTER.getName(), master);
        multi.put(DataSourceEnum.SLAVE.getName(), slave);

        dataSourceRouter.setDefaultTargetDataSource(master);
        dataSourceRouter.setTargetDataSources(multi);
        return dataSourceRouter;
    }

    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(@Qualifier("dataSourceRouter") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public ServletRegistrationBean statViewServlet() {
        ServletRegistrationBean<StatViewServlet> servletRegistrationBean = new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");
        //设置ip白名单
        servletRegistrationBean.addInitParameter(StatViewServlet.PARAM_NAME_ALLOW, allow);
        //设置ip黑名单，如果allow与deny共同存在时,deny优先于allow
        servletRegistrationBean.addInitParameter(StatViewServlet.PARAM_NAME_DENY, deny);
        //设置控制台管理用户
        servletRegistrationBean.addInitParameter(StatViewServlet.PARAM_NAME_USERNAME, loginUsername);
        servletRegistrationBean.addInitParameter(StatViewServlet.PARAM_NAME_PASSWORD, loginPassword);
        //是否可以重置数据
        servletRegistrationBean.addInitParameter(StatViewServlet.PARAM_NAME_RESET_ENABLE, resetEnable);
        return servletRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean webStatFilter() {
        FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(new WebStatFilter());
        filterFilterRegistrationBean.addInitParameter(WebStatFilter.PARAM_NAME_EXCLUSIONS, exclusions);
        filterFilterRegistrationBean.addUrlPatterns(urlPatterns);
        return filterFilterRegistrationBean;
    }


}
