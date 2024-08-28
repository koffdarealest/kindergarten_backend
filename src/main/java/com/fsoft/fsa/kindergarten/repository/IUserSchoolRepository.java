package com.fsoft.fsa.kindergarten.repository;

import com.fsoft.fsa.kindergarten.model.entity.School;
import com.fsoft.fsa.kindergarten.model.entity.User;
import com.fsoft.fsa.kindergarten.model.entity.UserSchool;
import com.fsoft.fsa.kindergarten.model.entity.UserSchoolId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IUserSchoolRepository extends JpaRepository<UserSchool, UserSchoolId>, JpaSpecificationExecutor<UserSchool> {
    @Query("SELECT u FROM UserSchool u WHERE u.users.id = :userId")
    List<UserSchool> findByUserId(@Param("userId") Integer userId);

    UserSchool findUserSchoolByUsersAndSchools(User user, School school);
}
