package com.fsoft.fsa.kindergarten.service.imp;

import com.fsoft.fsa.kindergarten.config.exception.ResourceNotFoundException;
import com.fsoft.fsa.kindergarten.model.dto.rating.RatingSummary;
import com.fsoft.fsa.kindergarten.model.entity.*;
import com.fsoft.fsa.kindergarten.model.form.feedback.FeedbackRatingForm;
import com.fsoft.fsa.kindergarten.model.form.rating.RatingForm;
import com.fsoft.fsa.kindergarten.repository.*;
import com.fsoft.fsa.kindergarten.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RatingServiceImp implements RatingService {
    private final JwtService jwtService;
    private final IRatingRepository ratingRepository;
    private final IUserRepository userRepository;
    private final ICriteriaRepository criteriaRepository;
    private final ISchoolRepository schoolRepository;
    private final IFeedbackRepository feedbackRepository;

    @Override
    public Map<String, Object> getAverageRatingsBySchoolIdAndDateRange(Integer schoolId, Date startDate, Date endDate) {
        List<RatingSummary> ratings;
        if (startDate != null && endDate != null) {
            ratings = ratingRepository.findAverageRatingsBySchoolIdAndDateRange(schoolId, startDate, endDate);
        } else {
            ratings = ratingRepository.findAverageRatingsBySchoolId(schoolId);
        }
        Integer countUserRating = ratingRepository.countUserRating(schoolId);
        Map<String, Object> result = new HashMap<>();
        result.put("ratings", ratings);
        result.put("startDate", startDate);
        result.put("endDate", endDate);
        result.put("totalRating", countUserRating);
        return result;
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "feedbackBySchoolPublicSide", allEntries = true),
                    @CacheEvict(value = "school", allEntries = true),
            }
    )
    public ResponseEntity<?> ratingAndFeedbackForSchool(FeedbackRatingForm request) {
        try {

            Integer activeUserId = jwtService.getIdFromToken();
            User user = userRepository.findById(activeUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            School school = schoolRepository.findById(request.getSchoolId())
                    .orElseThrow(() -> new ResourceNotFoundException("School not found"));
            // Add feedback
            Feedback existingFeedbackOpt = feedbackRepository.findFeedbacksBySchoolIdAndUserId(school.getId(),
                    user.getId());
            Feedback feedback;
            if(existingFeedbackOpt != null) {
                feedback = existingFeedbackOpt;
                feedback.setContent(request.getContent());
                feedback.setUpdatedAt(new Date());
            } else {
                feedback = new Feedback();
                feedback.setContent(request.getContent());
                feedback.setUser(user);
                feedback.setSchool(school);
            }
            feedbackRepository.save(feedback);

            //Add rating
            if(request.getRatings() != null) {
                for (RatingForm item : request.getRatings()) {
                    Rating rating = new Rating();
                    Optional<Criteria> criteriaOpt = criteriaRepository.findById(item.getCriteriaId());
                    if (criteriaOpt.isPresent()) {
                        Criteria criteria = criteriaOpt.get();
                        Optional<Rating> existingRatingOpt = ratingRepository.findByUserAndSchoolAndCriteria(user, school,
                                criteria);
                        if (existingRatingOpt.isPresent()) {
                            rating = existingRatingOpt.get();
                            rating.setValue(item.getValue());

                        } else {
                            rating = new Rating();
                            rating.setValue(item.getValue());
                            rating.setCriteria(criteria);
                            rating.setUser(user);
                            rating.setSchool(school);
                        }
                        ratingRepository.save(rating);
                    } else {
                        throw new ResourceNotFoundException("Criteria not found");
                    }
                }
            }
            return ResponseEntity.ok("Feedback and ratings submitted successfully.");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while submitting feedback and ratings.");
        }
    }
}
