package com.zkd.demo.interceptor;

import com.zkd.demo.annotation.CusIngoreDs;
import com.zkd.demo.config.CusDsLoadConfig;
import com.zkd.demo.holder.CusDsContextHolder;
import com.zkd.demo.properties.CusDsConfigProperties;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @date 2023/8/8
 */
public class CusDsWebInterceptor implements HandlerInterceptor {

    private CusDsConfigProperties cusDsConfigProperties;
    private CusDsLoadConfig cusDsLoadConfig;

    public CusDsWebInterceptor(CusDsConfigProperties cusDsConfigProperties, CusDsLoadConfig cusDsLoadConfig) {
        this.cusDsConfigProperties = cusDsConfigProperties;
        this.cusDsLoadConfig = cusDsLoadConfig;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!cusDsConfigProperties.getEnableWebPreHandle()) {
            return true;
        }
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            if (method.getAnnotation(CusIngoreDs.class) != null) {
                return true;
            }
            String dataSource = request.getHeader(cusDsConfigProperties.getDetermineDsParam());
            if (StringUtils.hasText(dataSource) && cusDsLoadConfig != null) {
                cusDsLoadConfig.determineDs(dataSource);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 在控制器方法执行之后，视图渲染之前执行的代码
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 在整个请求处理完成后执行的代码，包括视图渲染之后,清除调当前数据源线程持有
        CusDsContextHolder.removeAll();
    }
}
