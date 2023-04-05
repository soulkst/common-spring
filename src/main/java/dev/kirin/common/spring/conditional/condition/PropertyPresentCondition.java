package dev.kirin.common.spring.conditional.condition;

import dev.kirin.common.core.utils.StringUtil;
import dev.kirin.common.spring.conditional.ConditionalOnPropertyPresent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.env.*;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Map;

@Slf4j
public class PropertyPresentCondition implements Condition {
    private static final String VALUE_ATTRIBUTE_NAME = "value";

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return containsKey(context.getEnvironment(), getProperty(metadata));
    }

    private String getProperty(AnnotatedTypeMetadata metadata) {
        MergedAnnotation<ConditionalOnPropertyPresent> mergedAnnotation = metadata.getAnnotations().get(ConditionalOnPropertyPresent.class);
        if(!mergedAnnotation.isPresent()) {
            throw new IllegalArgumentException("Only support 'ConditionalOnPropertyPresent'");
        }
        return mergedAnnotation.getString(VALUE_ATTRIBUTE_NAME);
    }

    private boolean containsKey(Environment environment, String key) {
        MutablePropertySources propertySources = ((AbstractEnvironment) environment).getPropertySources();
        for (PropertySource<?> propertySource : propertySources) {
            if (!(propertySource instanceof MapPropertySource)) {
                continue;
            }
            Map<String, Object> sourceMap = ((MapPropertySource) propertySource).getSource();
            for (String sourceKey : sourceMap.keySet()) {
                String[] sourceKeyItems = sourceKey.split(StringUtil.ESCAPE_REGEX_DOT);
                if (sourceKeyItems.length > 0) {
                    StringBuilder last = new StringBuilder(sourceKeyItems[0]);
                    for (int i = 1; i < sourceKeyItems.length; i++) {
                        last.append(StringUtil.DOT).append(sourceKeyItems[i]);
                        if (last.toString().equals(key)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
