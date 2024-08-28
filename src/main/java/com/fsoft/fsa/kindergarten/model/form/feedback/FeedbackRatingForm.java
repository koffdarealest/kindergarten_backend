package com.fsoft.fsa.kindergarten.model.form.feedback;

import com.fsoft.fsa.kindergarten.model.form.rating.RatingForm;
import com.fsoft.fsa.kindergarten.model.validation.school.SchoolError;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class FeedbackRatingForm {
    @NotNull(message = "School id cannot be null")
    @Min(1)
    @SchoolError
    private Integer schoolId;

    @NotBlank(message = "content can not blank")
    private String content;

    @Valid
    private List<RatingForm> ratings;
}
