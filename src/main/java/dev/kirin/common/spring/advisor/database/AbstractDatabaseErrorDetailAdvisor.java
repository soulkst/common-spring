package dev.kirin.common.spring.advisor.database;

import dev.kirin.common.spring.extension.message.ErrorMessageHandler;
import dev.kirin.common.spring.model.vo.ApiErrorVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public abstract class AbstractDatabaseErrorDetailAdvisor {
    @Autowired
    protected ErrorMessageHandler errorMessageHandler;

    public ResponseEntity<ApiErrorVo> handleDataIntegrityViolationException(HttpServletRequest request, DataIntegrityViolationException e) {
        return null;
    }
}
