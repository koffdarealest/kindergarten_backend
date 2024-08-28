package com.fsoft.fsa.kindergarten.service.imp;

import com.fsoft.fsa.kindergarten.model.dto.feedback.FeedbackDTO;
import com.fsoft.fsa.kindergarten.model.dto.page.PageResponse;
import com.fsoft.fsa.kindergarten.model.dto.rating.RatingSummary;
import com.fsoft.fsa.kindergarten.model.entity.Feedback;
import com.fsoft.fsa.kindergarten.repository.IFeedbackRepository;
import com.fsoft.fsa.kindergarten.repository.IRatingRepository;
import com.fsoft.fsa.kindergarten.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImp implements FeedbackService {

    private final IFeedbackRepository feedbackRepository;
    private final IRatingRepository ratingRepository;

    @Override
    @Cacheable(value = "feedbackBySchoolAdminSide", key = "#schoolId + '_' + #startDate + '_' + #endDate" +
            " + '_' + #limit + '_' +T(java.util.Arrays).toString(#ratingRange)")
    public Map<String, Object> getFeedbacksBySchoolAndAverageRating(Integer schoolId, Date startDate,
                                                                    Date endDate, int[] ratingRange, int limit) {
        List<Feedback> feedbacks;

        if (startDate != null && endDate != null) {
            feedbacks = feedbackRepository.findFeedbacksBySchoolIdAndDateRange(schoolId, startDate, endDate);
        } else {
            feedbacks = feedbackRepository.findFeedbacksBySchoolId(schoolId);
        }
        List<FeedbackDTO> feedbackDTOs = feedbacks.stream()
                .map(feedback -> {
                    List<RatingSummary> ratings = ratingRepository.findAverageRatingsBySchoolOfUser(schoolId, feedback.getUser().getId());
                    Double averageRatingValue = ratings.isEmpty() ? null : ratings.stream().mapToDouble(RatingSummary::getAverageRatingValue).average().orElse(0.0);
                    return new AbstractMap.SimpleEntry<>(feedback, new AbstractMap.SimpleEntry<>(averageRatingValue, ratings));
                })
                .filter(entry -> {
                    Double averageRatingValue = entry.getValue().getKey();
                    // Nếu averageRatingValue là null và ratingRange chứa số 0, thì lấy feedback này
                    if (averageRatingValue == null) {
                        return Arrays.stream(ratingRange).anyMatch(rating -> rating == 0);
                    }
                    // Nếu averageRatingValue không null, chỉ lấy feedbacks có averageRatingValue nằm trong ratingRange
                    return Arrays.stream(ratingRange).anyMatch(rating -> rating == Math.round(averageRatingValue));
                })
                .map(entry -> {
                    Feedback feedback = entry.getKey();
                    List<RatingSummary> ratings = entry.getValue().getValue();
                    return FeedbackDTO.builder()
                            .id(feedback.getId())
                            .name(feedback.getUser().getUserName())
                            .avatar(feedback.getUser().getAvatar())
                            .content(feedback.getContent())
                            .createDate(feedback.getCreatedAt())
                            .ratings(ratings)
                            .averageCriteriaValue(entry.getValue().getKey())
                            .build();
                })
                .limit(limit)
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("feedbacks", feedbackDTOs);
        result.put("ratingRange", ratingRange);
        result.put("startDate", startDate);
        result.put("limit", limit);
        result.put("totalFeedback", feedbackDTOs.size());
        result.put("endDate", endDate);
        return result;
    }

    @Override
    @Cacheable(value = "feedbackBySchoolPublicSide", key = "#schoolId + '_' + #pageable.pageNumber + '_' + #pageable.pageSize  " +
            "+ '_' + T(java.util.Arrays).toString(#ratingRange)")
    public PageResponse<?> getFeedbacksBySchool(Integer schoolId, Pageable pageable, int[] ratingRange) {
        List<Feedback> feedbacks = feedbackRepository.findFeedbacksBySchoolId(schoolId);
        List<FeedbackDTO> feedbackDTOs = feedbacks.stream()
                .map(feedback -> {
                    List<RatingSummary> ratings = ratingRepository.findAverageRatingsBySchoolOfUser(schoolId, feedback.getUser().getId());
                    Double averageRatingValue = ratings.isEmpty() ? null : ratings.stream().mapToDouble(RatingSummary::getAverageRatingValue).average().orElse(0.0);
                    return new AbstractMap.SimpleEntry<>(feedback, new AbstractMap.SimpleEntry<>(averageRatingValue, ratings));
                })
                .filter(entry -> {
                    Double averageRatingValue = entry.getValue().getKey();
                    // Nếu averageRatingValue là null và ratingRange chứa số 0, thì lấy feedback này
                    if (averageRatingValue == null) {
                        return Arrays.stream(ratingRange).anyMatch(rating -> rating == 0);
                    }
                    // Nếu averageRatingValue không null, chỉ lấy feedbacks có averageRatingValue nằm trong ratingRange
                    return Arrays.stream(ratingRange).anyMatch(rating -> rating == Math.round(averageRatingValue));
                })
                .map(entry -> {
                    Feedback feedback = entry.getKey();
                    List<RatingSummary> ratings = entry.getValue().getValue();
                    return FeedbackDTO.builder()
                            .id(feedback.getId())
                            .name(feedback.getUser().getUserName())
                            .avatar(feedback.getUser().getAvatar())
                            .content(feedback.getContent())
                            .createDate(feedback.getUpdatedAt() != null
                                    ? feedback.getUpdatedAt()
                                    :feedback.getCreatedAt())
                            .ratings(ratings)
                            .averageCriteriaValue(entry.getValue().getKey())
                            .build();
                })
                .collect(Collectors.toList());
        int start = Math.min(pageable.getPageNumber() * pageable.getPageSize(), feedbackDTOs.size());
        int end = Math.min(start + pageable.getPageSize(), feedbackDTOs.size());
        List<FeedbackDTO> paginatedList = new ArrayList<>(feedbackDTOs.subList(start, end));

        return PageResponse.builder()
                .pageNo(pageable.getPageNumber() + 1)
                .pageSize(pageable.getPageSize())
                .totalPage((int) Math.ceil((double) feedbackDTOs.size() / pageable.getPageSize()))
                .totalElement(feedbackDTOs.size())
                .item(paginatedList)
                .build();
    }

    @Override
    public FeedbackDTO getFeedbacksBySchoolAndAverageRating(Integer schoolId, Integer userId) {
        Feedback feedbacks = feedbackRepository.findFeedbacksBySchoolIdAndUserId(schoolId, userId);
        Double averageRating = ratingRepository.findAverageRatingBySchoolIdAndUserId(schoolId, userId);

        if(feedbacks == null) return null;
        return FeedbackDTO.builder()
                .id(feedbacks.getId())
                .name(feedbacks.getUser().getFullName())
                .avatar(feedbacks.getUser().getAvatar())
                .content(feedbacks.getContent())
                .createDate(feedbacks.getCreatedAt())
                .averageCriteriaValue(averageRating)
                .build();
    }

    @Override
    public Map<String, Object> getTopFeedbacks(int limit, int average) {
        List<Feedback> feedbacks = feedbackRepository.getAllFeedback();
        List<FeedbackDTO> feedbackDTOs = feedbacks.stream()
                .map(feedback -> {
                    Double averageRating = ratingRepository.findAverageRatingBySchoolIdAndUserId(feedback.getSchool().getId(), feedback.getUser().getId());
                    return new AbstractMap.SimpleEntry<>(feedback, averageRating);
                })
                .filter(entry -> {
                    Double averageRating = entry.getValue();
                    if(averageRating != null) {
                        return (average == Math.round(averageRating));
                    } else {
                        return (average == 0);
                    }
                })
                .sorted(Comparator.comparing((AbstractMap.SimpleEntry<Feedback, Double> entry)
                        -> entry.getKey().getCreatedAt()).reversed())
                .map(entry -> {
                    Feedback feedback = entry.getKey();
                    Double averageRating = entry.getValue();

                    return FeedbackDTO.builder()
                            .id(feedback.getId())
                            .name(feedback.getUser().getUserName())
                            .avatar(feedback.getUser().getAvatar())
                            .content(feedback.getContent())
                            .createDate(feedback.getCreatedAt())
                            .averageCriteriaValue(averageRating) // có thể là null
                            .build();
                })
                .limit(limit)
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("feedbacks", feedbackDTOs);
        result.put("average", average);
        result.put("limit", limit);
        result.put("totalFeedback", feedbackDTOs.size());
        return result;
    }
}
