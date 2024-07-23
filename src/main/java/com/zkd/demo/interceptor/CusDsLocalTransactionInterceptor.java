package com.zkd.demo.interceptor;

import com.baomidou.dynamic.datasource.tx.LocalTxUtil;
import com.baomidou.dynamic.datasource.tx.TransactionContext;
import com.zkd.demo.annotation.CusDsTransactional;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.StringUtils;

/**
 * @author ZhangLi
 * @date 2023/10/25
*/
@Slf4j
public class CusDsLocalTransactionInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {

        if (StringUtils.hasText(TransactionContext.getXID())) {
            return methodInvocation.proceed();
        }
        boolean state = true;
        Object o;
        LocalTxUtil.startTransaction();
        try {
            o = methodInvocation.proceed();
        } catch (Exception e) {
            CusDsTransactional cusDsTransactional = methodInvocation.getMethod().getAnnotation(CusDsTransactional.class);
            if (cusDsTransactional == null) {
                cusDsTransactional = methodInvocation.getMethod().getDeclaringClass().getAnnotation(CusDsTransactional.class);
            }
            Class<? extends Throwable>[] exClasses = cusDsTransactional.rollbackFor();
            for (Class<? extends Throwable> exClass : exClasses) {
                if (exClass.isInstance(e)) {
                    state = false;
                    break;
                }
            }
            throw e;
        } finally {
            if (state) {
                LocalTxUtil.commit();
            } else {
                LocalTxUtil.rollback();
            }
        }
        return o;
    }
}
