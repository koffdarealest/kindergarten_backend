package com.fsoft.fsa.kindergarten.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
public class RequestDTO implements Serializable {
    private Integer id;

    private String fullName;

    private String email;

    private String phone;

    private String question;

    private String status;

    private String schoolName;
}
