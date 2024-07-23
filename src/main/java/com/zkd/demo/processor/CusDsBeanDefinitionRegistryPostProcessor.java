package com.zkd.demo.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionAttribute;

import java.lang.reflect.Method;


@Component
@Slf4j
public class CusDsBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {

    private static final String TRANSACTION_ATTRIBUTE_SOURCE_BEAN_NAME = "transactionAttributeSource";
    private static final String ENABLE_TRANSACTIONAL_ANNOTATION = "cus-dynamic-data-source.enableTransactionalAnnotation";

    private Environment environment;

    /**
     * {@link org.springframework.transaction.annotation.ProxyTransactionManagementConfiguration#transactionAttributeSource()}
     *
     * @param registry
     * @throws BeansException
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        String strValue = environment.getProperty(ENABLE_TRANSACTIONAL_ANNOTATION);
        if (Boolean.TRUE.equals(Boolean.valueOf(strValue))) {
            log.warn("Using the Spring @Transactional annotation has been enabled. please be aware that using this annotation may lead to multi-datasource transaction issues.");
            return;
        }
        registry.removeBeanDefinition(TRANSACTION_ATTRIBUTE_SOURCE_BEAN_NAME);
        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.rootBeanDefinition(AnnotationTransactionAttributeSource.class)
                .setRole(BeanDefinition.ROLE_INFRASTRUCTURE).getBeanDefinition();
        beanDefinition.setInstanceSupplier(() -> new AnnotationTransactionAttributeSource() {

            @Override
            public TransactionAttribute getTransactionAttribute(Method method, Class<?> targetClass) {
                return null;
            }

        });
        registry.registerBeanDefinition(TRANSACTION_ATTRIBUTE_SOURCE_BEAN_NAME, beanDefinition);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
