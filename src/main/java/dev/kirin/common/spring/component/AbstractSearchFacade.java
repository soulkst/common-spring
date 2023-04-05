package dev.kirin.common.spring.component;

import dev.kirin.common.spring.model.dto.DTOModel;
import dev.kirin.common.spring.model.dto.SearchParamDTOModel;
import dev.kirin.common.spring.model.vo.PageVo;
import dev.kirin.common.spring.model.vo.SearchParamVoModel;
import dev.kirin.common.spring.model.vo.VoModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractSearchFacade<D extends DTOModel<?, ?>, S extends SearchParamDTOModel<?>
        , V extends VoModel<D>, P extends SearchParamVoModel<S>
        , Service extends AbstractFixedService<?, ?, D, S, ?>> {

    @Autowired
    protected Service service;

    protected abstract V toVo(D dto);

    public List<V> list(P searchParam) {
        return service.list(searchParam.toSearchParam())
                .stream()
                .map(this::toVo)
                .collect(Collectors.toList());
    }

    public PageVo<V> page(P searchParam, Pageable pageable) {
        S param = searchParam.toSearchParam(pageable);
        Page<D> page = service.page(param);
        return PageVo.of(page, this::toVo);
    }
}
