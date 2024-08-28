package com.fsoft.fsa.kindergarten.model.dto.feedback;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fsoft.fsa.kindergarten.model.dto.rating.RatingSummary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class FeedbackDTO implements Serializable {
        private Integer id;
        private String name;
        private String avatar;
        private String content;
        private Date createDate;
        private Double averageCriteriaValue;
        private List<RatingSummary> ratings;
}