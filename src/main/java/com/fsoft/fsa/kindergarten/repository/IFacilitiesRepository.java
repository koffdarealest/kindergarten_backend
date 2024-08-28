package com.fsoft.fsa.kindergarten.repository;

import com.fsoft.fsa.kindergarten.model.entity.Facilities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IFacilitiesRepository extends JpaRepository<Facilities, Integer>, JpaSpecificationExecutor<Facilities> {
}
