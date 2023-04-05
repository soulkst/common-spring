package dev.kirin.common.spring.advisor.database;

import dev.kirin.common.spring.conditional.ConditionalOnDatabase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.annotation.PostConstruct;

@Slf4j
@RestController
@RestControllerAdvice
@ConditionalOnDatabase(ConditionalOnDatabase.DataBase.POSTGRESQL)
public class PostgreSQLDetailErrorAdvisor extends AbstractDatabaseErrorDetailAdvisor {
    @PostConstruct
    void postConstruct() {
        log.info("(postgreSQL-advisor) Enabled");
    }
}
