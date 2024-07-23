/*
package com.zkd.demo.util;

import cn.hutool.core.codec.Base32;
import cn.hutool.core.collection.CollUtil;
import com.aliyun.centralhub.schedule.connector.core.exception.SourceException;
import com.aliyun.centralhub.schedule.connector.dbz.jdbc.JdbcSinkConnectorConfig;
import com.google.common.base.Throwables;
import io.debezium.custom.logger.BaseTaskLogger;
import io.debezium.custom.logger.ConnectorTaskRunLogger;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.c3p0.internal.C3P0ConnectionProvider;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.stat.Statistics;
import org.hibernate.tool.schema.Action;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


*/
/**
 * @author: xiaosl
 * @date: 2024-06-03-19:13
 * @description:
 *//*

public class HibernateAndPoolUtil {
    private static final Log log = LogFactory.getLog(HibernateC3p0PoolUtil.class);

    private static volatile Map<String, Map<Long, StatelessSession>> sessionMap = new ConcurrentHashMap<>();
    private static volatile Map<String, SessionFactory> sessionFactoryMap = new ConcurrentHashMap<>();
    private static volatile Map<String, StopWatch> stopWatchMap = new ConcurrentHashMap<>();


    */
/**
     * 获取指定数据库的session会话信息
     *//*

    public static StatelessSession getSession(JdbcSinkConnectorConfig config) {
        String uniqueKey = getUniqueKey(config);
        long threadId = Thread.currentThread().getId();
        Map<Long, StatelessSession> threadSessionMap = sessionMap.getOrDefault(uniqueKey, new ConcurrentHashMap<>());
        StatelessSession statelessSession = threadSessionMap.getOrDefault(threadId, initSession(config, uniqueKey, threadId));
        resetStopWatch(uniqueKey);
        return statelessSession;
    }

    */
/**
     * 获取指定数据库的session factory信息
     *//*

    public static SessionFactory getSessionFactory(JdbcSinkConnectorConfig config) {
        String uniqueKey = getUniqueKey(config);
        SessionFactory sessionFactory = sessionFactoryMap.get(uniqueKey);
        if (null != sessionFactory*/
/* && checkAndRemoveSessionFactory(sessionFactory, uniqueKey)*//*
) {
            //重置时间
            resetStopWatch(uniqueKey);
            return sessionFactory;
        }
        return initSessionFactory(config, uniqueKey);
    }

    */
/**
     * 初始化 session会话信息
     *//*

    private static synchronized StatelessSession initSession(JdbcSinkConnectorConfig config, String uniqueKey, long threadId) {
        Map<Long, StatelessSession> threadSessionMap = sessionMap.getOrDefault(uniqueKey, new ConcurrentHashMap<>());
        StatelessSession cacheSession = threadSessionMap.get(threadId);
        if (null != cacheSession) {
            return cacheSession;
        }
        StatelessSession statelessSession = getSessionFactory(config).openStatelessSession();
        threadSessionMap.put(threadId, statelessSession);
        sessionMap.put(uniqueKey, threadSessionMap);
        //计时器开始计时,这里可能计时器已经在初始化SessionFactory已经开始计时了
        StopWatch stopWatch = stopWatchMap.getOrDefault(uniqueKey, new StopWatch());
        if (!stopWatch.isStarted()) {
            stopWatch.start();
        }
        stopWatchMap.putIfAbsent(uniqueKey, stopWatch);
        return statelessSession;
    }

    */
/**
     * 初始化session factory信息
     *//*

    private static synchronized SessionFactory initSessionFactory(JdbcSinkConnectorConfig config, String uniqueKey) {
        SessionFactory cacheSessionFactory = sessionFactoryMap.get(uniqueKey);
        if (null != cacheSessionFactory) {
            return cacheSessionFactory;
        }
        SessionFactory sessionFactory = null;
        try {
            sessionFactory = getHibernateConfig(config).buildSessionFactory();
        } catch (Exception e) {
            String errorMessage = String.format("目标端【数据库连接池初始化阶段】构建sessionFactory失败,失败原因：%s", Throwables.getStackTraceAsString(e));
            new ConnectorTaskRunLogger(config.getCentralhubTaskId(), config.getCentralhubTaskRunId(), config.getSyncAll(), config.getTableIncludeStr()).sinkLog(errorMessage, false);
            throw new SourceException(errorMessage, e);
        }
        if (log.isTraceEnabled()) {
            Statistics statistics = sessionFactory.getStatistics();
            Long activeCount = statistics.getSessionOpenCount() - statistics.getSessionCloseCount();
            new ConnectorTaskRunLogger(config.getCentralhubTaskId(), config.getCentralhubTaskRunId(), config.getSyncAll(), config.getTableIncludeStr()).sinkLog(BaseTaskLogger.PHASE_SINK_INIT + String.format("成功开启源端数据库的 Stateless Session,SessionOpenCount:%s,SessionCloseCount:%s,activeCount:%s", statistics.getSessionOpenCount(), statistics.getSessionCloseCount(), activeCount));

        }
        sessionFactoryMap.put(uniqueKey, sessionFactory);
        //计时器开始计时
        StopWatch stopWatch = stopWatchMap.getOrDefault(uniqueKey, new StopWatch());
        stopWatch.start();
        stopWatchMap.put(uniqueKey, stopWatch);
        log.info(">>>>>>>>>>创建sessionFactory成功");
        return sessionFactory;
    }


    private static synchronized void resetStopWatch(String uniqueKey) {
        StopWatch stopWatch = stopWatchMap.get(uniqueKey);
        if (null != stopWatch) {
            stopWatch.reset();
            stopWatch.start();
        }
    }


    private static Configuration getHibernateConfig(JdbcSinkConnectorConfig config) {
        Configuration hibernateConfig = new Configuration();
        //设置数据库连接信息
        hibernateConfig.setProperty(AvailableSettings.URL, config.getConfig().getString(CONNECTION_URL_FIELD));
        hibernateConfig.setProperty(AvailableSettings.USER, config.getConfig().getString(CONNECTION_USER_FIELD));
        hibernateConfig.setProperty(AvailableSettings.PASS, config.getConfig().getString(CONNECTION_PASSWORD_FIELD));
        //将SQL中的标识符（表名，列名等）全部使用引号括起来
        hibernateConfig.setProperty(AvailableSettings.GLOBALLY_QUOTED_IDENTIFIERS, "true");
        //设置时区
        hibernateConfig.setProperty(AvailableSettings.JDBC_TIME_ZONE, "UTC");
        hibernateConfig.setProperty("hibernate.is-connection-validation-required", "true");

        //设置打印SQL
        hibernateConfig.setProperty(AvailableSettings.SHOW_SQL, Boolean.toString(true));
        hibernateConfig.setProperty(AvailableSettings.FORMAT_SQL, Boolean.toString(true));
        //采用何种方式生成DDL语句，update表示检测实体类的映射配置与数据库表结构是否一致，不一致，则更新数据库。
        hibernateConfig.setProperty(AvailableSettings.HBM2DDL_AUTO, Action.NONE.getExternalJpaName());
        hibernateConfig.setProperty("connection.autoReconnect", "true");
        hibernateConfig.setProperty("connection.autoReconnectForPools", "true");
        try {
            hibernateConfig.setProperty("hibernate.dialect", config.getConfig().getString("hibernate.dialect"));
            hibernateConfig.setProperty("hibernate.connection.driver_class", config.getConfig().getString("hibernate.connection.driver_class"));
        } catch (Exception e) {
            log.error("get property failed, please check it. error: {}", e);
        }


        //初始化连接池相关参数
        initCp30PoolConfig(hibernateConfig, config);
        return hibernateConfig;
    }

    */
/**
     * 初始化C3P0连接池属性
     * <p>
     * C3P0连接池部分重要源码：
     * 连接池初始化的默认值源码：{@com.mchange.v2.c3p0.impl.WrapperConnectionPoolDataSourceBase}
     * 用户配置覆盖默认配置源码283行：{@com.mchange.v2.c3p0.DataSources#pooledDataSource(javax.sql.DataSource, java.lang.String, java.util.Map)}
     * C3P0日志输出源码：{ com.mchange.v2.c3p0.impl.WrapperConnectionPoolDataSourceBase#toString()}
     * 官方文档：<a href="https://www.mchange.com/projects/c3p0/#maxIdleTime">...</a>
     *
     * @param hibernateConfig
     *//*


    private static void initCp30PoolConfig(Configuration hibernateConfig, JdbcSinkConnectorConfig config) {
        //选择使用C3P0连接池
        hibernateConfig.setProperty(AvailableSettings.CONNECTION_PROVIDER, C3P0ConnectionProvider.class.getName());
        // 连接池中最小连接数
        hibernateConfig.setProperty(AvailableSettings.C3P0_MIN_SIZE, config.getConfig().getString(CONNECTION_POOL_MIN_SIZE_FIELD));
        //连接池中最大连接数
        hibernateConfig.setProperty(AvailableSettings.C3P0_MAX_SIZE, config.getConfig().getString(CONNECTION_POOL_MAX_SIZE_FIELD));
        //初始化时获取指定数量连接，取值应在minPoolSize与maxPoolSize之间。Default: 3
        hibernateConfig.setProperty("initialPoolSize", "1");

        //最大空闲时间,30秒内未使用则连接被丢弃。若为0则永不丢弃。Default: 0；此AvailableSettings.C3P0_TIMEOUT在C3P0连接池实际赋值属性名称还是maxIdleTime，AvailableSettings.C3P0_TIMEOUT优先级较高
        hibernateConfig.setProperty(AvailableSettings.C3P0_TIMEOUT, "30");
        //hibernateConfig.setProperty("maxIdleTime", "30");
        //这个表示连接池检测线程多长时间检测一次池内的所有链接对象是否超时；AvailableSettings.C3P0_IDLE_TEST_PERIOD和idleConnectionTestPeriod效果一致
        hibernateConfig.setProperty(AvailableSettings.C3P0_IDLE_TEST_PERIOD, "30");
        //当连接池中的连接耗尽的时候c3p0一次同时获取的连接数。Default: 3
//        hibernateConfig.setProperty(AvailableSettings.C3P0_ACQUIRE_INCREMENT, "1");
        //定义 c3p0 在放弃之前从数据库获取新连接失败后将重试的次数。如果 此值小于零，则 c3p0 将继续尝试无限期地获取连接。Default: 30
        hibernateConfig.setProperty("acquireRetryAttempts", "10");
        //两次连接中间隔时间，单位毫秒。Default: 1000
        hibernateConfig.setProperty("acquireRetryDelay", "1000");
        //!!!当连接池用完时客户端调用getConnection()后等待获取新连接的时间，超时后将抛出SQLException,如设为0则无限期等待。单位毫秒。Default: 0;其实就是acquireRetryAttempts*acquireRetryDelay。default : 0（与上面两个，有重复，选择其中两个都行）
        hibernateConfig.setProperty("checkoutTimeout", "60000");

        //连接关闭时默认将所有未提交的操作回滚。Default: false，但c3p0会自动回滚
        hibernateConfig.setProperty("autoCommitOnClose", Boolean.toString(Boolean.FALSE));
        //如果设为true那么在取得连接的同时将校验连接的有效性。Default: false
        hibernateConfig.setProperty("testConnectionOnCheckin", Boolean.toString(Boolean.TRUE));
        //因性能消耗大请只在需要的时候使用它。如果设为true那么在每个connection提交的时候都将校验其有效性。建议使用idleConnectionTestPeriod或automaticTestTable    等方法来提升连接测试的性能。Default: false
        hibernateConfig.setProperty("testConnectionOnCheckout", Boolean.toString(Boolean.FALSE));

    }

    */
/**
     * 校验数据库session是否有效
     *//*

    private static boolean checkAndRemoveSession(StatelessSession session, String key) {
        if (!session.isOpen() || !session.isConnected()) {
            sessionMap.remove(key);
            return false;
        }
        return true;
    }

    */
/**
     * 校验数据库session是否有效
     *//*

    */
/*private static boolean checkAndRemoveSessionFactory(SessionFactory sessionFactory, String key) {
        if (sessionFactory.isClosed()) {
            StatelessSession statelessSession = sessionMap.get(key);
            if (null != statelessSession) {
                statelessSession.close();
            }
            sessionMap.remove(key);
            sessionFactoryMap.remove(key);
            return false;
        }

        return true;
    }*//*


    */
/**
     * 获取数据库连接池缓存的唯一key
     *//*

    private static String getUniqueKey(JdbcSinkConnectorConfig config) {
        String jdbcUrl = config.getConfig().getString(CONNECTION_URL_FIELD);
        String userName = config.getConfig().getString(CONNECTION_USER_FIELD);
        String passWord = config.getConfig().getString(CONNECTION_PASSWORD_FIELD);
        return Base32.encode(jdbcUrl + "|" + userName + "|" + passWord);
    }

    */
/**
     * 关闭连接池
     *//*

    private static void closeDataSource(String uniqueKey) {
        Map<Long, StatelessSession> threadSessionMap = sessionMap.get(uniqueKey);
        if (CollUtil.isNotEmpty(threadSessionMap)) {
            threadSessionMap.forEach((threadId, session) -> session.close());
            threadSessionMap.clear();
            sessionMap.remove(uniqueKey);
            if (log.isTraceEnabled()) {
                log.info("关闭数据库「" + Base32.decodeStr(uniqueKey) + "」对应的session会话成功");
            }
        }
        SessionFactory sessionFactory = sessionFactoryMap.get(uniqueKey);
        if (null != sessionFactory) {
            sessionFactoryMap.remove(uniqueKey);
            sessionFactory.close();
            if (log.isTraceEnabled()) {
                log.info("关闭数据库「" + Base32.decodeStr(uniqueKey) + "」对应的sessionFactory会话成功");
            }
        }
    }

    */
/**
     * 关闭线程维度的session会话
     *//*

    public static void closeThreadSession(JdbcSinkConnectorConfig config, boolean fail) {
        try {
            String uniqueKey = getUniqueKey(config);
            long threadId = Thread.currentThread().getId();
            Map<Long, StatelessSession> threadSessionMap = sessionMap.get(uniqueKey);
            if (fail) {
                log.info(String.format("任务「%s」线程「%s」获取连接或者执行SQL失败，开始关闭session会话", config.getCentralhubTaskId(), threadId));
            }
            if (CollUtil.isNotEmpty(threadSessionMap)) {
                StatelessSession statelessSession = threadSessionMap.get(threadId);
                if (null != statelessSession) {
                    try {
                        statelessSession.close();
                    } catch (Exception e) {
                        log.error(String.format("关闭线程「%s」对应的session会话异常，异常信息为：%s", threadId, e.getMessage()));
                    }
                    threadSessionMap.remove(threadId);
                    if (log.isTraceEnabled()) {
                        log.info("关闭线程「" + threadId + "」对应的session会话成功");
                    }
                }
            }
        }catch (Exception e){
            log.error("closeThreadSession error", e);
        }


    }


    private volatile static PoolListener instance = null;
    private volatile static AtomicLong globClosePoolTime = new AtomicLong();
    private volatile static AtomicLong globCheckClosePoolTimeInterval = new AtomicLong();

    public static PoolListener getInstance(long closePoolTime, long checkClosePoolTimeInterval) {
        globClosePoolTime.set(closePoolTime);
        globCheckClosePoolTimeInterval.set(checkClosePoolTimeInterval);
        log.info("初始化数据库连接池监听器，默认全局数据库空闲时间为：" + globClosePoolTime.get() + "秒");
        //第一次校验
        if (null == instance) {
            synchronized (PoolListener.class) {
                //第二次校验
                if (null == instance) {
                    instance = new PoolListener();
                    Thread poolListenerThread = new Thread(instance, "SinkPoolListenerThread");
                    poolListenerThread.start();
                }
            }
        }
        return instance;
    }

    */
/**
     * 连接池监听器，对于空闲连接池进行关闭
     *//*

    private static class PoolListener implements Runnable {
        @Override
        public void run() {
            while (true) {
                stopWatchMap.forEach((uniqueKey, stopWatch) -> {
                    try {
                        log.info("数据库连接池监听器，当前已监听时间：" + stopWatch.toString());
                        if (stopWatch.isStopped() || !stopWatch.isStarted()) {
                            return;
                        }
                        stopWatch.split();
                        long splitTime = stopWatch.getSplitTime();
                        if (splitTime >= globClosePoolTime.get() * 1000) {
                            closeDataSource(uniqueKey);
                            stopWatch.stop();
                            stopWatchMap.remove(uniqueKey);
                            log.info("已成功关闭「" + Base32.decodeStr(uniqueKey) + "线程池");
                        }
                        Thread.sleep(globCheckClosePoolTimeInterval.get() * 1000);
                    } catch (Exception e) {
                        log.error("目标端数据库连接池监听器监听异常，异常信息：{}", e);
                    }

                });
            }
        }
    }

}
*/
