package com.fsoft.fsa.kindergarten.service;

import com.fsoft.fsa.kindergarten.model.dto.feedback.FeedbackDTO;
import com.fsoft.fsa.kindergarten.model.dto.page.PageResponse;
import com.fsoft.fsa.kindergarten.model.entity.Feedback;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public interface FeedbackService {
    Map<String, Object> getFeedbacksBySchoolAndAverageRating(Integer schoolId, Date startDate,
                                                                    Date endDate, int[] ratingRange, int limit);

    PageResponse<?> getFeedbacksBySchool(Integer schoolId, Pageable pageable, int[] ratingRange);

    FeedbackDTO getFeedbacksBySchoolAndAverageRating(Integer schoolId, Integer userId);

    Map<String, Object> getTopFeedbacks(int limit, int average);
}
