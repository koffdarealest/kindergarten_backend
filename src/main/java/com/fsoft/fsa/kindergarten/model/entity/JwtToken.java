package com.fsoft.fsa.kindergarten.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "t_jwt_token")
@Accessors(chain = true)
public class JwtToken extends BaseEntity{

    @ManyToOne
    private User user;

    @Column(name = "access_token", length = 300)
    private String accessToken;

    @Column(name= "expiry_time")
    private Date accessTokenExpiryTime;

    @Column(name = "is_revoked")
    private Boolean isRevoked;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "refresh_token_expiry_time")
    private Date refreshTokenExpiryTime;
}
