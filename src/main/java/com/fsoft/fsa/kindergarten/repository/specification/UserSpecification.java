package com.fsoft.fsa.kindergarten.repository.specification;

import com.fsoft.fsa.kindergarten.model.entity.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    public static Specification<User> hasEmail(String email) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("email"), email));
    }

    public static Specification<User> containsTextInEmailNamePhoneOrStatus(String search) {
        return (root, query, criteriaBuilder) -> {
            Predicate isNotDeleted = criteriaBuilder.equal(root.get("isDeleted"), false);

            if (search == null || search.isEmpty()) {
                return isNotDeleted;
            }
            String likePattern = "%" + search.toLowerCase()
                    .replace("%", "\\%")
                    .replace("_", "\\_").trim()
                    + "%";
            return criteriaBuilder.and(
                    isNotDeleted, criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), likePattern),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("fullName")), likePattern),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("phone")), likePattern),
                            criteriaBuilder.equal(criteriaBuilder.lower(root.get("status")), search.toLowerCase().trim())
                    )
            );
        };
    }

    public static Specification<User> isRole(Integer roleId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("role").get("id"), roleId);
    }


}
