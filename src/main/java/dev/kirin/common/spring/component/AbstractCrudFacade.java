package dev.kirin.common.spring.component;

import dev.kirin.common.spring.model.dto.DTOModel;
import dev.kirin.common.spring.model.dto.SearchParamDTOModel;
import dev.kirin.common.spring.model.vo.SearchParamVoModel;
import dev.kirin.common.spring.model.vo.VoModel;

public abstract class AbstractCrudFacade<ID
        , D extends DTOModel<ID, ?>, S extends SearchParamDTOModel<?>
        , V extends VoModel<D>, P extends SearchParamVoModel<S>
        , Service extends AbstractCrudService<ID, ?, D, S, ?>>
        extends AbstractFixedFacade<ID, D, S, V, P, Service>
        implements DomainBasedComponent {

    public V create(V param) {
        D dto = service.upsert(null, param.toDTO());
        return toVo(dto);
    }

    @Override
    public V update(ID id, V param) {
        D dto = service.upsert(id, param.toDTO());
        return toVo(dto);
    }

    public void delete(ID id) {
        service.delete(id);
    }
}
