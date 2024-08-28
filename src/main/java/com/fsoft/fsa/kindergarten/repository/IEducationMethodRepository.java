package com.fsoft.fsa.kindergarten.repository;

import com.fsoft.fsa.kindergarten.model.entity.EducationMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IEducationMethodRepository extends JpaRepository<EducationMethod, Integer>, JpaSpecificationExecutor<EducationMethod> {
}
