package dev.kirin.common.spring.constraint.validator;

import dev.kirin.common.spring.constraint.annotation.EnhancedNotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collection;
import java.util.Map;

@Slf4j
public class EnhancedNotEmptyConstraintValidator implements ConstraintValidator<EnhancedNotEmpty, Object> {
    private static final String VALID_PATTERN = "^(null|undefined)$";

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if(value == null) {
            return false;
        }

        if(value instanceof String) {
            String valueString = (String) value;
            return StringUtils.hasText(valueString) && !valueString.matches(VALID_PATTERN);
        }
        if(value.getClass().isArray()) {
            Object[] valueArray = (Object[]) value;
            return valueArray.length > 0;
        }
        if(value instanceof Collection) {
            return CollectionUtils.isEmpty((Collection<?>) value);
        }
        if(value instanceof Map) {
            return CollectionUtils.isEmpty((Map<?, ?>) value);
        }

        return true;
    }
}
