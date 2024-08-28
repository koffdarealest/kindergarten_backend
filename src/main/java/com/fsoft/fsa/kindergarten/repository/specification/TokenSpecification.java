package com.fsoft.fsa.kindergarten.repository.specification;

import com.fsoft.fsa.kindergarten.model.entity.Token;
import org.springframework.data.jpa.domain.Specification;


import java.util.Date;

public class TokenSpecification {
    public static Specification<Token> hasToken(String token) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("token"), token));
    }

    public static Specification<Token> isNotExpired() {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get("expiryTime"), new Date()));
    }

    public static Specification<Token> isNotUsed() {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.isFalse(root.get("isUsed")));
    }
}
