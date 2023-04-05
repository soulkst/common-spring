package dev.kirin.common.spring.component;

import dev.kirin.common.spring.exception.NotFoundException;
import dev.kirin.common.spring.model.dto.DTOModel;
import dev.kirin.common.spring.model.dto.SearchParamDTOModel;
import dev.kirin.common.spring.model.vo.SearchParamVoModel;
import dev.kirin.common.spring.model.vo.VoModel;

public abstract class AbstractFixedFacade<ID
        , D extends DTOModel<ID, ?>, S extends SearchParamDTOModel<?>
        , V extends VoModel<D>, P extends SearchParamVoModel<S>
        , Service extends AbstractFixedService<ID, ?, D, S, ?>>
        extends AbstractSearchFacade<D, S, V, P, Service>
        implements DomainBasedComponent {

    public V update(ID id, V param) {
        if(service.get(id) == null) {
            throw new NotFoundException(getDomainName(), id);
        }
        D dto = service.upsert(id, param.toDTO());
        return toVo(dto);
    }

    public V get(ID id) {
        D dto = service.get(id);
        if(dto == null) {
            throw new NotFoundException(getDomainName(), id);
        }
        return toVo(dto);
    }

    @Override
    public String getDomainName() {
        return service.getDomainName();
    }
}
