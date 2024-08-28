package com.fsoft.fsa.kindergarten.controller;

import com.fsoft.fsa.kindergarten.model.dto.page.PageResponse;
import com.fsoft.fsa.kindergarten.model.dto.page.RequestPageResponse;
import com.fsoft.fsa.kindergarten.model.dto.request.RequestDTO;
import com.fsoft.fsa.kindergarten.model.form.request.RequestForm;
import com.fsoft.fsa.kindergarten.model.validation.request.RequestStatus;
import com.fsoft.fsa.kindergarten.service.RequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/request")
@Validated
@Log4j2
@Tag(name = "Request Controller")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RequestController {

    private final RequestService requestService;

    @Operation(summary = "Get list request", description = "API get list request have search and pagination")
    @GetMapping("/list")
    public PageResponse<?> getAllRequest(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") @Max(10) int pageSize,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "createdAt", defaultValue = "desc") String sortBy
    ) {
        log.info("Request get all request sortBy = " + sortBy);
        return requestService.getListRequest(pageNo, pageSize, search, sortBy);
    }

    @Operation(summary = "Get list request", description = "API get list request have search and pagination")
    @GetMapping("/public/myRequest")
    public RequestPageResponse<?> getAllMyRequest(
            @RequestParam(defaultValue = "1") @Min(1) int pageNo,
            @RequestParam(defaultValue = "10") @Max(10) int pageSize
    ) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return requestService.getMyRequest(pageable);
    }

    @Operation(summary = "Get request", description = "API get request by id")
    @GetMapping("/{requestId}")
    public RequestDTO getRequest(@PathVariable int requestId) {
        log.info("Request get request id = " + requestId);
        return requestService.getRequest(requestId);
    }

    @Operation(summary = "Change request status", description = "API Changing request status by requestId")
    @PatchMapping("{requestId}")
    public String changeRequestStatus(
            @Min(1) @PathVariable int requestId,
            @RequestParam RequestStatus status
    ) {
        log.info("Request change successfully");
        requestService.changeStatus(requestId, status);
        return "Request change successfully";
    }

    @Operation(summary = "Get list request reminder", description = "API get list request reminder have search and pagination")
    @GetMapping("/reminder")
    public PageResponse<?> getAllRequestReminder(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") @Max(10) int pageSize,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "createdAt", defaultValue = "desc") String sortBy
    ) {
        log.info("Request get all request reminder sortBy = " + sortBy);
        return requestService.getListRequestReminder(pageNo, pageSize, search, sortBy);
    }

    @Operation(summary = "Create new request", description = "API add new request")
    @PostMapping("/public")
    public String createRequest(@Valid @RequestBody RequestForm requestForm){
        log.info("Create new request");
        requestService.createRequest(requestForm);
        return "Create request successfully";
    }

    @Operation(summary = "Create new request", description = "API get number request by status for user")
    @GetMapping("/countStatus")
    public Integer getNumberRequestByStatusForUser(@RequestParam(defaultValue = "Open", required = false) String status){
        log.info("Get total request by status");
        return requestService.getNumberRequestOfStatus(status);
    }

}
