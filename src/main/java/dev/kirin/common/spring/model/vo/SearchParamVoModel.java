package dev.kirin.common.spring.model.vo;

import dev.kirin.common.spring.model.dto.SearchParamDTOModel;
import org.springframework.data.domain.Pageable;

public interface SearchParamVoModel<S extends SearchParamDTOModel<?>> {
    default S toSearchParam() {
        return toSearchParam(null);
    }

    S toSearchParam(Pageable pageable);
}
