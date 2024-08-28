package com.fsoft.fsa.kindergarten.repository;


import com.fsoft.fsa.kindergarten.model.dto.page.PageResponse;
import com.fsoft.fsa.kindergarten.model.entity.School;
import com.fsoft.fsa.kindergarten.model.entity.SchoolStatus;
import com.fsoft.fsa.kindergarten.repository.criteria.SearchCriteria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Repository
public class SearchRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public PageResponse<?> advanceSearchByCriteria(int pageNo, int pageSize, String sortBy, String search) {
        List<SearchCriteria> criteriaList = new ArrayList<>();
        if (search != null) {
            criteriaList.add(new SearchCriteria("name", ":", "%" + search + "%"));
            criteriaList.add(new SearchCriteria("email", ":", "%" + search + "%"));
            criteriaList.add(new SearchCriteria("phone", ":", "%" + search + "%"));
            criteriaList.add(new SearchCriteria("status", ":", "%" + search + "%"));
        }

        Page<School> schools = getSchool(pageNo, pageSize, criteriaList, sortBy);

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .item(schools.getContent())
                .build();
    }

    private Page<School> getSchool(int pageNo, int pageSize, List<SearchCriteria> criteriaList, String sortBy) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<School> query = cb.createQuery(School.class);
        Root<School> schoolRoot = query.from(School.class);
        Join<School, SchoolStatus> statusJoin = schoolRoot.join("status", JoinType.LEFT);

        Predicate predicate = cb.conjunction();

        for (SearchCriteria criteria : criteriaList) {
            if (criteria.getKey().equals("status.name")) {
                predicate = cb.and(predicate, cb.like(statusJoin.get("name"), criteria.getValue().toString()));
            } else {
                predicate = cb.and(predicate, cb.like(schoolRoot.get(criteria.getKey()), criteria.getValue().toString()));
            }
        }

        query.where(predicate);

        // Sort by createdDate
        if (StringUtils.hasLength(sortBy) && sortBy.equalsIgnoreCase("desc")) {
            query.orderBy(cb.desc(schoolRoot.get("createdDate")));
        } else {
            query.orderBy(cb.asc(schoolRoot.get("createdDate")));
        }

        List<School> result = entityManager.createQuery(query)
                .setFirstResult(pageNo * pageSize)
                .setMaxResults(pageSize)
                .getResultList();

        // Total count for pagination
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<School> countRoot = countQuery.from(School.class);
        countRoot.join("status", JoinType.LEFT);
        countQuery.select(cb.count(countRoot)).where(predicate);
        Long count = entityManager.createQuery(countQuery).getSingleResult();

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return new PageImpl<>(result, pageable, count);
    }
}
