package com.fsoft.fsa.kindergarten.repository.specification;

import com.fsoft.fsa.kindergarten.model.entity.Facilities;
import com.fsoft.fsa.kindergarten.model.entity.School;
import com.fsoft.fsa.kindergarten.model.entity.Utilities;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class SchoolSpecification {

    public static Specification<School> containsTextInEmailNamePhoneOrStatus(String search) {
        return (root, query, criteriaBuilder) -> {
            if (search == null || search.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            String likePattern = "%" + search.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), likePattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), likePattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("phone")), likePattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("status").get("status")), likePattern)
            );
        };
    }

    public static Specification<School> isNotSaveStatusOrOwnedByUser(Integer userId) {
        return (root, query, criteriaBuilder) -> {
            Predicate notSaveStatus = criteriaBuilder.notEqual(root.get("status").get("status"), "SAVED");

            // Tạo predicate cho trạng thái "save" và thuộc về user hiện tại
            Predicate saveStatusAndOwnedByUser = criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("status").get("id"), 1),
                    criteriaBuilder.equal(root.get("schoolOwnerId"), userId)
            );

            // Kết hợp hai predicate bằng OR
            return criteriaBuilder.or(notSaveStatus, saveStatusAndOwnedByUser);
        };
    }

    public static Specification<School> belongsToUser(Integer userId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("schoolOwnerId"), userId);
    }

    public static Specification<School> belongsToPublished(){
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status").get("id"), 5 );
    }

    public static Specification<School> filter(String search, String province, String city, Integer schoolType,
                                               Integer schoolAge, Integer feeFrom, Integer feeTo,
                                               Integer[] facilities, Integer[] utilities) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            //get school public status
            predicates.add(criteriaBuilder.equal(root.get("status").get("id"), 5));
            if (search != null && !search.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + search + "%"));
            }
            if (province != null && !province.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("district"), province));
            }
            if (city != null && !city.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("city"), city));
            }
            if (schoolType != null) {
                predicates.add(criteriaBuilder.equal(root.get("type").get("id"), schoolType));
            }
            if (schoolAge != null) {
                predicates.add(criteriaBuilder.equal(root.get("age").get("id"), schoolAge));
            }
            //feeFrom gia truyen vao >= freeFrom cua school
            if (feeFrom != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("feeFrom"), feeFrom));
            }
            //feeTo gia truyen vao <= freeFrom cua school
            if (feeTo != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("feeTo"), feeTo));
            }

            if (facilities != null && facilities.length > 0) {
                Join<School, Facilities> facilitiesJoin = root.join("facilities", JoinType.INNER);
                predicates.add(facilitiesJoin.get("id").in(facilities));
            }

            if (utilities != null && utilities.length > 0) {
                Join<School, Utilities> utilitiesJoin = root.join("utilities", JoinType.INNER);
                predicates.add(utilitiesJoin.get("id").in(utilities));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
