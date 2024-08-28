package com.fsoft.fsa.kindergarten.controller;

import com.fsoft.fsa.kindergarten.model.form.feedback.FeedbackRatingForm;
import com.fsoft.fsa.kindergarten.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rating")
@Validated
@Log4j2
@Tag(name = "Feedback Controller")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RatingController {

    private final RatingService ratingService;

    @Operation(summary = "Rating and feedback for school", description = "API send request rating for school")
    @PostMapping
    public ResponseEntity<?> giveRatingAndFeedback(@Valid @RequestBody FeedbackRatingForm feedbackRatingForm) {
        return ratingService.ratingAndFeedbackForSchool(feedbackRatingForm);
    }
}
