package dev.kirin.common.spring.model;

public interface EntityModel<ID> {
    ID getId();
    void setId(ID id);
}
