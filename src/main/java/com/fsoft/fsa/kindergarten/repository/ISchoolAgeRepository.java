package com.fsoft.fsa.kindergarten.repository;

import com.fsoft.fsa.kindergarten.model.entity.SchoolAge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ISchoolAgeRepository extends JpaRepository<SchoolAge, Integer>, JpaSpecificationExecutor<SchoolAge> {

}
