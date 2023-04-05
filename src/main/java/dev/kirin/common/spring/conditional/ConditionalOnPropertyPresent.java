package dev.kirin.common.spring.conditional;

import dev.kirin.common.spring.conditional.condition.PropertyPresentCondition;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Documented
@Conditional(PropertyPresentCondition.class)
public @interface ConditionalOnPropertyPresent {
    String value();
}
