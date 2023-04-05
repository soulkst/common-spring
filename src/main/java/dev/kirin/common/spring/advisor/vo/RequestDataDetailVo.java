package dev.kirin.common.spring.advisor.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RequestDataDetailVo {
    private HttpHeaders headers;
    private String body;

    public static RequestDataDetailVo of(HttpHeaders headers, String body) {
        return RequestDataDetailVo.builder()
                .headers(headers)
                .body(body)
                .build();
    }
}
