package dev.kirin.common.spring.advisor.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class BindExceptionDetailVo {
    private Type type;
    private String objectName;
    private String field;
    private String defaultMessage;
    private Object rejectedValue;

    public static BindExceptionDetailVo of(ObjectError objectError) {
        if(objectError instanceof FieldError) {
            return ofField((FieldError) objectError);
        }
        return BindExceptionDetailVo.builder()
                .type(Type.OBJECT)
                .objectName(objectError.getObjectName())
                .defaultMessage(objectError.getDefaultMessage())
                .build();
    }

    public static BindExceptionDetailVo ofField(FieldError fieldError) {
        return BindExceptionDetailVo.builder()
                .type(Type.FIELD)
                .objectName(fieldError.getObjectName())
                .field(fieldError.getField())
                .defaultMessage(fieldError.getDefaultMessage())
                .rejectedValue(fieldError.getRejectedValue())
                .build();
    }
    public static List<BindExceptionDetailVo> ofAll(Collection<ObjectError> allErrors) {
        if(CollectionUtils.isEmpty(allErrors)) {
            return null;
        }
        return allErrors.stream()
                .map(BindExceptionDetailVo::of)
                .collect(Collectors.toList());
    }
    public enum Type {
        OBJECT, FIELD
    }
}
