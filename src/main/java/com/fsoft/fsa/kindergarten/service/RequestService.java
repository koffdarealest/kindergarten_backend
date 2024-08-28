package com.fsoft.fsa.kindergarten.service;

import com.fsoft.fsa.kindergarten.model.dto.page.PageResponse;
import com.fsoft.fsa.kindergarten.model.dto.page.RequestPageResponse;
import com.fsoft.fsa.kindergarten.model.dto.request.RequestDTO;
import com.fsoft.fsa.kindergarten.model.form.request.RequestForm;
import com.fsoft.fsa.kindergarten.model.validation.request.RequestStatus;
import org.springframework.data.domain.Pageable;

public interface RequestService {

    PageResponse<?> getListRequest(int page, int size, String search, String sortBy);

    RequestPageResponse<?> getMyRequest(Pageable pageable);

    PageResponse<?> getListRequestReminder(int page, int size, String search, String sortBy);

    RequestDTO getRequest(int requestId);

    void changeStatus(int requestId, RequestStatus status);

    void createRequest(RequestForm requestForm);

    Integer getNumberRequestOfStatus(String status);
}
