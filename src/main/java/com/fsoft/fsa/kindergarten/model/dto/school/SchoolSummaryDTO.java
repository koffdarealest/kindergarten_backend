package com.fsoft.fsa.kindergarten.model.dto.school;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
public class SchoolSummaryDTO implements Serializable {
    private Integer id;
    private String name;
    private String addressLine;
    private String ward;
    private String district;
    private String city;
    private Double feeFrom;
    private Double feeTo;
    private SchoolAgeDTO schoolAge;
    private double averageRating;
    private Integer totalRating;
}
