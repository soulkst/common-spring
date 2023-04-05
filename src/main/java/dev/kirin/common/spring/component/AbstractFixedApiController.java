package dev.kirin.common.spring.component;

import dev.kirin.common.spring.constraint.UpdateValidGroup;
import dev.kirin.common.spring.constraint.annotation.EnhancedNotEmpty;
import dev.kirin.common.spring.model.vo.SearchParamVoModel;
import dev.kirin.common.spring.model.vo.VoModel;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
public class AbstractFixedApiController<ID
        , V extends VoModel<?>, S extends SearchParamVoModel<?>
        , F extends AbstractFixedFacade<ID, ?, ?, V, S, ?>> extends AbstractSearchApiController<V, S, F> {

    @PutMapping(value = "/{id}")
    @Operation(summary = "Update/Modify")
    public V update(@PathVariable("id") @EnhancedNotEmpty ID id
            , @Validated(UpdateValidGroup.class) @RequestBody V body) {
        return facade.update(id, body);
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "Get detail")
    public V get(@PathVariable("id") ID id) {
        return facade.get(id);
    }
}
