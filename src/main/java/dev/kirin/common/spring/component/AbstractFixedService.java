package dev.kirin.common.spring.component;

import dev.kirin.common.core.utils.StringUtil;
import dev.kirin.common.spring.exception.InvalidArgumentException;
import dev.kirin.common.spring.exception.NotFoundException;
import dev.kirin.common.spring.model.EnhancedPageable;
import dev.kirin.common.spring.model.EntityModel;
import dev.kirin.common.spring.model.dto.DTOModel;
import dev.kirin.common.spring.model.dto.SearchParamDTOModel;
import dev.kirin.common.spring.util.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractFixedService<ID, T extends EntityModel<ID>
        , D extends DTOModel<ID, T>, S extends SearchParamDTOModel<T>
        , R extends SimpleRepository<ID, T>> implements DomainBasedComponent {
    @Autowired
    protected R repository;

    protected abstract D toDTO(T entity);

    protected String uuid() {
        return UUID.randomUUID().toString().replaceAll(StringUtil.HYPHEN, StringUtil.BLANK);
    }
    @Transactional
    public D upsert(ID existsId, D param) {
        if(existsId == null) {
            throw new InvalidArgumentException(getDomainName(), "upsert.id", null);
        }

        T entityParam = param.toEntity();
        T entity = repository.findById(existsId)
                .orElseThrow(() -> new NotFoundException(getDomainName(), existsId));

        duplicateValidate(existsId, entityParam);
        BeanUtil.copyIfNotNulls(entityParam, entity);
        return toDTO(entity);
    }

    public D get(ID id) {
        T entity = repository.findById(id)
                .orElse(getDefaultEntity(id));
        return toDTO(entity);
    }

    public List<D> list(S searchParam)  {
        return repository.findAll(searchParam.asSpecification())
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Page<D> page(S searchParam) {
        Page<T> page = repository.findAll(searchParam.asSpecification(), searchParam.getPageable());
        List<D> contents = page.getContent()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(contents, new EnhancedPageable(page.getPageable(), page.getTotalElements()), page.getTotalElements());
    }

    protected T getDefaultEntity(ID id) {
        return null;
    }
    protected void duplicateValidate(ID id, T requested) {}
}
