package com.fsoft.fsa.kindergarten.repository;

import com.fsoft.fsa.kindergarten.model.entity.SchoolStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ISchoolStatusRepository extends JpaRepository<SchoolStatus, Integer>, JpaSpecificationExecutor<SchoolStatus> {
}
