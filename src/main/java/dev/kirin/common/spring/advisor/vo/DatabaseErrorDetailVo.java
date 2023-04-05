package dev.kirin.common.spring.advisor.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.SQLException;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class DatabaseErrorDetailVo {
    private String constraintName;
    private String sql;
    private String sqlState;
    private int errorCode;


    public static DatabaseErrorDetailVo of(SQLException e) {
        return DatabaseErrorDetailVo.builder()
                .sqlState(e.getSQLState())
                .errorCode(e.getErrorCode())
                .build();
    }
}
