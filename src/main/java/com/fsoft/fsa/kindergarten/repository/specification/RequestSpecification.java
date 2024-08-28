package com.fsoft.fsa.kindergarten.repository.specification;

import com.fsoft.fsa.kindergarten.model.entity.Request;
import com.fsoft.fsa.kindergarten.model.validation.request.RequestStatus;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class RequestSpecification {

    public static Specification<Request> containsTextInEmailNamePhoneOrStatus(String search) {
        return (root, query, criteriaBuilder) -> {
            if (search == null || search.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            String likePattern = "%" + search.toLowerCase()
                    .replace("%", "\\%")
                    .replace("_", "\\_").trim()
                    + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), likePattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("fullName")), likePattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("phone")), likePattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("status")), likePattern)
            );
        };
    }

    public static Specification<Request> statusOfSchool(String status) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Request> getRequestsByStatusForUser(Integer userId, String status) {
        return Specification.where(statusOfSchool(status)).and(statusRequestOfUser(userId));
    }

    public static Specification<Request> statusRequestOfUser(Integer userId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user").get("id"), userId);
    }

    public static Specification<Request> belongToUser(Integer userId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("school").get("schoolOwnerId"), userId);
    }

    public static Specification<Request> containsTextInEmailNameOrPhone(String search) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (search != null) {
                String likePattern = "%" + search.toLowerCase()
                        .replace("%", "\\%")
                        .replace("_", "\\_").trim()
                        + "%";
                predicates.add(
                        criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), likePattern),
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("fullName")), likePattern),
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("phone")), likePattern)
                        )
                );
            }

            predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("status")), RequestStatus.Open.toString()));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
