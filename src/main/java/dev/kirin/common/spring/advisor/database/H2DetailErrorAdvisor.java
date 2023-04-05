package dev.kirin.common.spring.advisor.database;

import dev.kirin.common.spring.advisor.vo.DatabaseErrorDetailVo;
import dev.kirin.common.spring.conditional.ConditionalOnDatabase;
import dev.kirin.common.spring.model.vo.ApiErrorVo;
import lombok.extern.slf4j.Slf4j;
import org.h2.api.ErrorCode;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

@Slf4j
@Component
@ConditionalOnDatabase(ConditionalOnDatabase.DataBase.H2)
public class H2DetailErrorAdvisor extends AbstractDatabaseErrorDetailAdvisor {
    @PostConstruct
    void postConstruct() {
        log.info("(h2-advisor) Enabled");
    }

    @Override
    public ResponseEntity<ApiErrorVo> handleDataIntegrityViolationException(HttpServletRequest request, DataIntegrityViolationException e) {
        Throwable rootCause = e.getRootCause();
        Throwable cause = e.getCause();

        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        DatabaseErrorDetailVo databaseErrorDetailVo = null;
        String title = null;
        Throwable errorType = e;
        if(cause instanceof org.hibernate.exception.ConstraintViolationException) {
            org.hibernate.exception.ConstraintViolationException ce = (ConstraintViolationException) cause;
            databaseErrorDetailVo = DatabaseErrorDetailVo.of(ce.getSQLException());
            switch (ce.getErrorCode()) {
                case ErrorCode.DUPLICATE_KEY_1:
                    errorType = ce;
                    databaseErrorDetailVo.setSql(ce.getConstraintName());
                    httpStatus = HttpStatus.CONFLICT;
                    title = errorMessageHandler.getTitle(request, DuplicateKeyException.class, e.getLocalizedMessage());
                    break;
                default:
                    break;
            }
        } else if(rootCause instanceof JdbcSQLIntegrityConstraintViolationException) {
            errorType = rootCause;
            databaseErrorDetailVo = DatabaseErrorDetailVo.of((SQLException) rootCause);
        } else {
            return null;
        }

        if(!StringUtils.hasText(title)) {
            title = errorMessageHandler.getTitle(request, errorType);
        }
        ApiErrorVo result = ApiErrorVo.conflict(request, errorType, title);
        result.setMore(databaseErrorDetailVo);

        log.error("(h2-handleDataIntegrityViolationException) uri = {}, cause = {}, response = {}", request.getRequestURI(), e.getLocalizedMessage(), result);
        log.debug("(h2-handleDataIntegrityViolationException) stack-trace", e);

        return new ResponseEntity<>(result, httpStatus);
    }
}
