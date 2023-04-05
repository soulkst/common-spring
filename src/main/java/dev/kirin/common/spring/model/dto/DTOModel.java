package dev.kirin.common.spring.model.dto;

public interface DTOModel<ID, T> {
    ID getId();
    T toEntity();
}
