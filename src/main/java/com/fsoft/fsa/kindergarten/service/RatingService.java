package com.fsoft.fsa.kindergarten.service;

import com.fsoft.fsa.kindergarten.model.form.feedback.FeedbackRatingForm;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public interface RatingService {
    Map<String, Object> getAverageRatingsBySchoolIdAndDateRange(Integer schoolId, Date startDate, Date endDate);

    ResponseEntity<?> ratingAndFeedbackForSchool(FeedbackRatingForm feedbackRatingForm);
}
