package com.fsoft.fsa.kindergarten.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "t_rating")
public class Rating extends BaseEntity{

    @Column(name = "criteria_value")
    private double value;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "criteria_id", referencedColumnName = "id")
    private Criteria criteria;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "school_id", referencedColumnName = "id")
    private School school;
}
