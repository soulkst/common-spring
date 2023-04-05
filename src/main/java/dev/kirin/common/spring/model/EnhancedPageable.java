package dev.kirin.common.spring.model;

import lombok.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Getter
@NoArgsConstructor
@ToString
public class EnhancedPageable implements Pageable {
    private int pageSize;
    private int pageNumber;
    private long offset;
    private Sort sort;
    private long totalPage;

    public EnhancedPageable(Pageable origin, long totalContents) {
        this.pageSize = origin.getPageSize();
        this.pageNumber = origin.getPageNumber();
        this.offset = origin.getOffset();
        this.sort = origin.getSort();
        this.totalPage = (long) Math.ceil((double) totalContents / this.pageSize);
    }

    private EnhancedPageable(Pageable origin, int pageNumber, long totalPage) {
        this.pageSize = origin.getPageSize();
        this.offset = origin.getOffset();
        this.sort = origin.getSort();
        this.pageNumber = pageNumber;
        this.totalPage = totalPage;
    }

    @Override
    public Pageable next() {
        return new EnhancedPageable(this, getPageNumber() + 1, getTotalPage());
    }

    @Override
    public Pageable previousOrFirst() {
        return new EnhancedPageable(this, Math.max(getPageNumber() - 1, 0), getTotalPage());
    }

    @Override
    public Pageable first() {
        return new EnhancedPageable(this, 0, getTotalPage());
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return new EnhancedPageable(this, pageNumber, getTotalPage());
    }

    @Override
    public boolean hasPrevious() {
        return getPageNumber() > 0;
    }
}
