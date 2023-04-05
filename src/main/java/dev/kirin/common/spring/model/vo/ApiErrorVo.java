package dev.kirin.common.spring.model.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.kirin.common.spring.exception.RestApiException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ApiErrorVo {
    private String type;
    private String title;
    private int status;
    private String detail;
    private String instance;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Object more;

    public static ApiErrorVo noContent(HttpServletRequest request, Throwable t, String title) {
        return of(HttpStatus.NO_CONTENT, request, t, title, null);
    }

    public static ApiErrorVo noContent(HttpServletRequest request, Throwable t, String title, String detail) {
        return of(HttpStatus.NO_CONTENT, request, t, title, detail);
    }

    public static ApiErrorVo notFound(HttpServletRequest request, Throwable t, String title) {
        return of(HttpStatus.NOT_FOUND, request, t, title, null);
    }
    public static ApiErrorVo notFound(HttpServletRequest request, Throwable t, String title, String detail) {
        return of(HttpStatus.NOT_FOUND, request, t, title, detail);
    }
    public static ApiErrorVo badRequest(HttpServletRequest request, Throwable t, String title) {
        return of(HttpStatus.BAD_REQUEST, request, t, title, null);
    }

    public static ApiErrorVo badRequest(HttpServletRequest request, Throwable t, String title, String detail) {
        return of(HttpStatus.BAD_REQUEST, request, t, title, detail);
    }

    public static ApiErrorVo conflict(HttpServletRequest request, Throwable t, String title) {
        return of(HttpStatus.CONFLICT, request, t, title, null);
    }

    public static ApiErrorVo conflict(HttpServletRequest request, Throwable t, String title, String detail) {
        return of(HttpStatus.CONFLICT, request, t, title, detail);
    }

    public static ApiErrorVo unknown(HttpServletRequest request, Throwable t, String title) {
        return of(HttpStatus.INTERNAL_SERVER_ERROR, request, t, title, null);
    }

    public static ApiErrorVo unknown(HttpServletRequest request, Throwable t, String title, String detail) {
        return of(HttpStatus.INTERNAL_SERVER_ERROR, request, t, title, detail);
    }

    public static ApiErrorVo of(HttpServletRequest request, RestApiException e, String title, String detail) {
        return of(e.getHttpStatus(), request, e, title, detail);
    }

    public static ApiErrorVo of(HttpStatus httpStatus, HttpServletRequest request, Throwable t, String title, String detail) {
        return ApiErrorVo.builder()
                .type(t.getClass().getName())
                .status(httpStatus.value())
                .instance(request.getRequestURI())
                .title(title)
                .detail(detail)
                .build();
    }
}
