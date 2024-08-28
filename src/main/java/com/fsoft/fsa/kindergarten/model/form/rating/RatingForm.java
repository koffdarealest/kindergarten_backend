package com.fsoft.fsa.kindergarten.model.form.rating;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingForm {
    @NotNull(message = "Criteria ID cannot be null")
    private Integer criteriaId;

    @Min(value = 1, message = "Rating value must be at least 1")
    @Max(value = 5, message = "Rating value must be at most 5")
    private double value;
}
