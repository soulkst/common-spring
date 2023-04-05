package dev.kirin.common.spring.advisor.vo;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class InvalidJsonFormatDetailVo {
    private Object value;
    private String path;
    private String location;
    private String targetType;

    public static InvalidJsonFormatDetailVo of(InvalidFormatException e) {
        return InvalidJsonFormatDetailVo.builder()
                .value(e.getValue())
                .path(e.getPathReference())
                .location(e.getLocation().toString())
                .targetType(e.getTargetType().getName())
                .build();
    }
}
