package com.fsoft.fsa.kindergarten.repository;

import com.fsoft.fsa.kindergarten.model.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface IFeedbackRepository extends JpaRepository<Feedback, Long> {
    @Query("SELECT f FROM Feedback f WHERE f.school.id = :schoolId AND f.createdAt BETWEEN :startDate AND :endDate")
    List<Feedback> findFeedbacksBySchoolIdAndDateRange(@Param("schoolId") Integer schoolId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT f FROM Feedback f WHERE f.school.id = :schoolId")
    List<Feedback> findFeedbacksBySchoolId(@Param("schoolId") Integer schoolId);

    @Query("SELECT f FROM Feedback f WHERE f.school.id = :schoolId AND f.user.id = :userId")
    Feedback findFeedbacksBySchoolIdAndUserId(@Param("schoolId") Integer schoolId, @Param("userId") Integer userId);

    @Query("SELECT f FROM Feedback f")
    List<Feedback> getAllFeedback();
}
