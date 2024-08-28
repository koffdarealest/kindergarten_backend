package com.fsoft.fsa.kindergarten.model.dto.request;

import com.fsoft.fsa.kindergarten.model.dto.school.SchoolSummaryDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
@Builder
@AllArgsConstructor
public class MyRequestDTO implements Serializable {
    private Integer requestId;
    private String fullName;
    private String email;
    private String phone;
    private String question;
    private String status;
    private Date requestDate;
    private SchoolSummaryDTO school;
}
