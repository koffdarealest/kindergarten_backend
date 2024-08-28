package com.fsoft.fsa.kindergarten.repository.specification;

import com.fsoft.fsa.kindergarten.model.entity.JwtToken;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;

public class JwtTokenSpecification {
    public static Specification<JwtToken> hasRefreshToken(String refreshToken) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("refreshToken"), refreshToken));
    }

    public static Specification<JwtToken> isNotRevoked() {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.isFalse(root.get("is_revoked")));
    }

    public static Specification<JwtToken> hasAccessToken(String accessToken) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("access_token"), accessToken));
    }

    public static Specification<JwtToken> isNotExpired() {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get("access_token_expiry_time"), new Date()));
    }

    public static Specification<JwtToken> hasUserId(Integer userId) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user").get("id"), userId));
    }
}
