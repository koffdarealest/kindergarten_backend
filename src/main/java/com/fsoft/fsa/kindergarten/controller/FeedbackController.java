package com.fsoft.fsa.kindergarten.controller;

import com.fsoft.fsa.kindergarten.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/feedback")
@Validated
@Log4j2
@Tag(name = "Feedback Controller")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @Operation(summary = "Get top feedback", description = "API get list request have search and pagination")
    @GetMapping("/top")
    public ResponseEntity<Map<String, Object>> getAllRequest(
            @RequestParam(defaultValue = "4", required = false) @Min(1) int limit,
            @RequestParam(defaultValue = "4", required = false) @Min(1) @Max(5) int average) {
        Map<String, Object> result = feedbackService.getTopFeedbacks(limit, average);
        return ResponseEntity.ok(result);
    }
}
