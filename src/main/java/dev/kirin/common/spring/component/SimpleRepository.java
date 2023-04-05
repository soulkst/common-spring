package dev.kirin.common.spring.component;

import dev.kirin.common.spring.model.EntityModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SimpleRepository<ID, T extends EntityModel<ID>> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {
}
