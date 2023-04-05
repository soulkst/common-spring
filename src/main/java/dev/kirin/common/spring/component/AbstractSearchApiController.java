package dev.kirin.common.spring.component;

import dev.kirin.common.spring.model.vo.VoModel;
import dev.kirin.common.spring.constraint.SearchValidGroup;
import dev.kirin.common.spring.model.vo.PageVo;
import dev.kirin.common.spring.model.vo.SearchParamVoModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Slf4j
@Validated
public class AbstractSearchApiController<V extends VoModel<?>, S extends SearchParamVoModel<?>
        , F extends AbstractSearchFacade<?, ?, V, S, ?>> {
    @Autowired
    protected F facade;

    @GetMapping
    @Operation(summary = "Pageable search")
    @PageableAsQueryParam
    public PageVo<V> search(@Parameter(hidden = true) Pageable pageRequest
            , @ParameterObject @Validated(SearchValidGroup.class) @ModelAttribute S searchParam) {
        log.debug("(search) requested pageable = {}, param = {}", pageRequest, searchParam);
        return facade.page(searchParam, pageRequest);
    }
}
