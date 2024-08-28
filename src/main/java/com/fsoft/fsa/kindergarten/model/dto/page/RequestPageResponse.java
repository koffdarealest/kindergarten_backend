package com.fsoft.fsa.kindergarten.model.dto.page;

import lombok.Builder;
import lombok.Getter;
import java.io.Serializable;

@Builder
@Getter
public class RequestPageResponse<T> implements Serializable {
    private int pageNo;
    private int pageSize;
    private int totalPage;
    private long totalElement;
    private long openElement;
    private T item;
}
