package dev.kirin.common.spring.advisor.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintViolation;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ConstraintViolationDetailVo {
    private String propertyPath;
    private String message;
    private Object invalidValue;

    public static ConstraintViolationDetailVo of(ConstraintViolation<?> constraintViolation) {
        return ConstraintViolationDetailVo.builder()
                .propertyPath(constraintViolation.getPropertyPath().toString())
                .message(constraintViolation.getMessage())
                .invalidValue(constraintViolation.getInvalidValue())
                .build();
    }

    public static List<ConstraintViolationDetailVo> ofAll(Collection<ConstraintViolation<?>> constraintViolations) {
        if(CollectionUtils.isEmpty(constraintViolations)) {
            return null;
        }
        return constraintViolations.stream()
                .map(ConstraintViolationDetailVo::of)
                .collect(Collectors.toList());
    }
}
