package dev.kirin.common.spring.conditional.condition;

import dev.kirin.common.spring.conditional.ConditionalOnDatabase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.type.AnnotatedTypeMetadata;

@Slf4j
public class DatabaseCondition implements Condition {
    private static final String PROPERTY_ATTRIBUTE_NAME = "property";
    private static final String VALUE_ATTRIBUTE_NAME = "value";

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        MergedAnnotation<ConditionalOnDatabase> mergedAnnotation = metadata.getAnnotations().get(ConditionalOnDatabase.class);
        if(!mergedAnnotation.isPresent()) {
            throw new IllegalArgumentException("Only support 'ConditionalOnPropertyValueContains'");
        }
        String property = context.getEnvironment().getProperty(mergedAnnotation.getString(PROPERTY_ATTRIBUTE_NAME));
        ConditionalOnDatabase.DataBase dataBase = mergedAnnotation.getEnum(VALUE_ATTRIBUTE_NAME, ConditionalOnDatabase.DataBase.class);
        return dataBase.is(property);
    }
}
