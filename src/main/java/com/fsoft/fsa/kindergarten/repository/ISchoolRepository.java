package com.fsoft.fsa.kindergarten.repository;

import com.fsoft.fsa.kindergarten.model.entity.School;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ISchoolRepository extends JpaRepository<School, Integer>, JpaSpecificationExecutor<School> {

    @Query("SELECT s FROM School s JOIN s.userSchools u WHERE u.users.id = :parentId")
    List<School> findSchoolByParentId(@Param("parentId") int parentId);

    @Query("SELECT s FROM School s JOIN s.userSchools u WHERE u.users.id = :parentId and u.status = true")
    List<School> findCurrentSchoolByParentId(@Param("parentId") int parentId);

    @Query("SELECT s FROM School s JOIN s.userSchools u WHERE u.users.id = :parentId and u.status = false")
    Page<School> findPreviousSchoolByParentId(@Param("parentId") int parentId, Pageable pageable);

    @Query("SELECT s FROM School s JOIN s.userSchools u WHERE u.users.id = :parentId and u.schools.id = :schoolId")
    Optional<School> findHaveSchoolParentEnroll(@Param("parentId") int parentId, @Param("schoolId") int schoolId);

    @Query("SELECT u.status FROM School s JOIN s.userSchools u WHERE u.users.id = :parentId and u.schools.id = :schoolId")
    Boolean isLearning(@Param("parentId") int parentId, @Param("schoolId") int schoolId);

}
