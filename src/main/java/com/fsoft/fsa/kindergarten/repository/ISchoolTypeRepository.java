package com.fsoft.fsa.kindergarten.repository;

import com.fsoft.fsa.kindergarten.model.entity.SchoolType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ISchoolTypeRepository extends JpaRepository<SchoolType, Integer>, JpaSpecificationExecutor<SchoolType> {
}
