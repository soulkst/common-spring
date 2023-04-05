package dev.kirin.common.spring.model.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.kirin.common.spring.model.dto.DTOModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class PageVo<V extends VoModel<?>> {
    private Pageable pageable;
    private long total;
    private List<V> contents;

    @JsonProperty(value = "hasContents", access = JsonProperty.Access.READ_ONLY)
    public boolean hasContents() {
        return !CollectionUtils.isEmpty(getContents());
    }

    public static <D extends DTOModel<?, ?>,  T extends VoModel<D>> PageVo<T> of(Page<D> page, Convert<D, T> converter) {
        List<T> contents  = page.getContent()
                .stream()
                .map(converter::convert)
                .collect(Collectors.toList());
        return PageVo.<T>builder()
                .total(page.getTotalElements())
                .pageable(page.getPageable())
                .contents(contents)
                .build();
    }

    public interface Convert<D, V> {
        V convert(D dto);
    }
}
