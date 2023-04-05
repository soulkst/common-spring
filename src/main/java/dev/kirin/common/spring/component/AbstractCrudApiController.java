package dev.kirin.common.spring.component;

import dev.kirin.common.spring.model.vo.VoModel;
import dev.kirin.common.spring.constraint.CreateValidGroup;
import dev.kirin.common.spring.model.vo.SearchParamVoModel;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
public class AbstractCrudApiController<ID
        , V extends VoModel<?>, S extends SearchParamVoModel<?>
        , F extends AbstractCrudFacade<ID, ?, ?, V, S, ?>> extends AbstractFixedApiController<ID, V, S, F> {
    @PostMapping
    @Operation(summary = "Create")
    @ResponseStatus(HttpStatus.CREATED)
    public V create(@Validated(CreateValidGroup.class) @RequestBody V body) {
        return facade.create(body);
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Delete")
    public void delete(@PathVariable("id") ID id) {
        facade.delete(id);
    }
}
