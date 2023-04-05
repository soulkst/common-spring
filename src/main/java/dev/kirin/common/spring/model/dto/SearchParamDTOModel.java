package dev.kirin.common.spring.model.dto;

import dev.kirin.common.core.utils.StringUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.List;

public interface SearchParamDTOModel<T> {
    String LIKE_CONDITION_FORMAT = "%{}%";

    List<Predicate> getPredicates(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder);

    Pageable getPageable();
    void setPageable(Pageable pageable);

    default String asLikeCondition(String value) {
        return StringUtil.format(LIKE_CONDITION_FORMAT, value);
    }

    default Specification<T> asSpecification() {
        return (root, query, builder) -> {
            List<Predicate> predicates = getPredicates(root, query, builder);
            if(predicates == null) {
                predicates =  Collections.emptyList();
            }
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
