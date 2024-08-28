package com.fsoft.fsa.kindergarten.model.dto.rating;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
public class RatingSummary implements Serializable {
    private Integer criteriaId;
    private String criteriaName;
    private Double averageRatingValue;
    private Long numberOfRatings;

    public RatingSummary(Integer criteriaId, String criteriaName, Integer averageRatingValue, Integer numberOfRatings) {
        this.criteriaId = criteriaId;
        this.criteriaName = criteriaName;
        this.averageRatingValue = (double)averageRatingValue;
        this.numberOfRatings = (long)numberOfRatings;
    }
}
