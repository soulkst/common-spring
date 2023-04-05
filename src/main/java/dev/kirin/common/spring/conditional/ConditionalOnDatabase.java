package dev.kirin.common.spring.conditional;

import dev.kirin.common.spring.conditional.condition.DatabaseCondition;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Conditional;
import org.springframework.util.StringUtils;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Documented
@Conditional(DatabaseCondition.class)
public @interface ConditionalOnDatabase {
    DataBase value();

    /**
     * jdbc URL property
     */
    String property() default "spring.datasource.url";

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    enum DataBase {
        H2, MYSQL, POSTGRESQL
        ;

        private final String pattern = ":" + name().toLowerCase() + ":";

        public boolean is(String value) {
            if(StringUtils.hasText(value)) {
                return value.contains(this.pattern);
            }
            return false;
        }
    }
}
