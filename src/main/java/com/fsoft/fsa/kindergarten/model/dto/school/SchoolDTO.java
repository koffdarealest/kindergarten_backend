package com.fsoft.fsa.kindergarten.model.dto.school;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.Date;
@Getter
@Builder
@AllArgsConstructor
public class SchoolDTO implements Serializable {
    private Integer id;
    private String schoolName;
    private String address;
    private String phone;
    private String email;
    private Date postedDate;
    private SchoolStatusDTO status;
}