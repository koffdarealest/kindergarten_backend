package com.fsoft.fsa.kindergarten.repository;

import com.fsoft.fsa.kindergarten.model.entity.Criteria;
import com.fsoft.fsa.kindergarten.model.entity.Facilities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ICriteriaRepository extends JpaRepository<Criteria, Integer>, JpaSpecificationExecutor<Facilities> {
}
