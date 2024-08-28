package com.fsoft.fsa.kindergarten.model.dto.school;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SchoolDetailWithRatingDTO {
    private SchoolDetailDTO schoolDetail;
    private Double rating;
}
