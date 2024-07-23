package com.zkd.demo.interceptor;


import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.zkd.demo.holder.CusDsContextHolder;
import com.zkd.demo.properties.CusDsConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 */
@ConditionalOnProperty(
        prefix = CusDsConfigProperties.PREFIX,
        havingValue = "true",
        name = "enable")
@Slf4j
@Component
@Intercepts(
        {
                @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        }
)
public class CusDsInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        boolean isPush = false;
        try {
            String peek = DynamicDataSourceContextHolder.peek();
            if (!StringUtils.hasText(peek)) {
                String currentThreadPrimaryDs = CusDsContextHolder.getCurrentThreadPrimaryDs();
                if (StringUtils.hasText(currentThreadPrimaryDs)) {
                    isPush = true;
                    DynamicDataSourceContextHolder.push(currentThreadPrimaryDs);
                }
            }
            return invocation.proceed();
        } finally {
            if (isPush) {
                DynamicDataSourceContextHolder.poll();
            }
        }
    }

}
