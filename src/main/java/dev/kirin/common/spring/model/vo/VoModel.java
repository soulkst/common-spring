package dev.kirin.common.spring.model.vo;

import dev.kirin.common.spring.model.dto.DTOModel;

public interface VoModel<D extends DTOModel<?, ?>> {
    D toDTO();
}
