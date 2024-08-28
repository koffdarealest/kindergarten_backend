package com.fsoft.fsa.kindergarten.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@NoArgsConstructor
@Entity
@Builder
@AllArgsConstructor
@Table(name = "t_token")
public class Token extends BaseEntity {

    @Column(name = "token")
    private String token;

    @Column(name = "expiry_time")
    private Date expiryTime;

    @Column(name = "is_used")
    private Boolean isUsed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
