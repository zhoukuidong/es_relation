package com.zkd.demo.basic.core.selector;

import com.zkd.demo.basic.annotation.EnableExceptionHandler;
import com.zkd.demo.basic.core.advice.DefaultExceptionAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Arrays;
import java.util.Map;

public class ExceptionHandlerSelector implements ImportSelector {
    private static final String VALUE = "value";
    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerSelector.class);

    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        if (annotationMetadata.hasAnnotation(EnableExceptionHandler.class.getName())) {
            Map<String, Object> annotationAttributes = annotationMetadata.getAnnotationAttributes(EnableExceptionHandler.class.getName());
            Class[] classes = (Class[]) annotationAttributes.get(VALUE);
            if (classes.length > 0) {
                String[] imports = new String[classes.length];
                for (int i = 0; i < classes.length; i++) {
                    imports[i] = classes[i].getName();
                }
                logger.info("{succeed to load advices:{}}", Arrays.toString(imports));
                return imports;
            }
            logger.info("{succeed to load DefaultExceptionAdvice}");
            return new String[]{DefaultExceptionAdvice.class.getName()};
        }
        return new String[0];
    }
}
