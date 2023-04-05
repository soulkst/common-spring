package dev.kirin.common.spring.component;

import dev.kirin.common.core.utils.StringUtil;
import dev.kirin.common.spring.exception.NotFoundException;
import dev.kirin.common.spring.model.EntityModel;
import dev.kirin.common.spring.model.dto.DTOModel;
import dev.kirin.common.spring.model.dto.SearchParamDTOModel;
import dev.kirin.common.spring.util.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
public abstract class AbstractCrudService<ID, T extends EntityModel<ID>
        , D extends DTOModel<ID, T>, S extends SearchParamDTOModel<T>
        , R extends SimpleRepository<ID, T>> extends AbstractFixedService<ID, T, D, S, R> {
    @Autowired
    protected R repository;

    protected abstract D toDTO(T entity);
    protected abstract ID newId(D param);

    protected String uuid() {
        return UUID.randomUUID().toString().replaceAll(StringUtil.HYPHEN, StringUtil.BLANK);
    }

    @Override
    @Transactional
    public D upsert(ID existsId, D param) {
        ID id = existsId;
        if(id == null) {
            id = newId(param);
            log.debug("(upsert) generated new id = {}", id);
        }
        T entityParam = param.toEntity();
        T entity = repository.findById(id)
                .orElse(null);
        duplicateValidate(id, entityParam);
        if(entity == null) {
            entityParam.setId(id);
            return toDTO(repository.save(entityParam));
        }
        BeanUtil.copyIfNotNulls(entityParam, entity);
        return toDTO(entity);
    }

    @Transactional
    public void delete(ID id) {
        T entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(getDomainName(), id));
        repository.delete(entity);
    }

    @Transactional
    public void deleteAll(S searchParam) {
        repository.deleteAll(repository.findAll(searchParam.asSpecification()));
    }
}
