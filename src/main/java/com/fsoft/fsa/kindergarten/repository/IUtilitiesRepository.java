package com.fsoft.fsa.kindergarten.repository;

import com.fsoft.fsa.kindergarten.model.entity.Utilities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IUtilitiesRepository extends JpaRepository<Utilities, Integer>, JpaSpecificationExecutor<Utilities> {

}
