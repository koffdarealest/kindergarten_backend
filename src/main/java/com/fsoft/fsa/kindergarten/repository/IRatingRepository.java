package com.fsoft.fsa.kindergarten.repository;

import com.fsoft.fsa.kindergarten.model.dto.rating.RatingSummary;
import com.fsoft.fsa.kindergarten.model.entity.Criteria;
import com.fsoft.fsa.kindergarten.model.entity.Rating;
import com.fsoft.fsa.kindergarten.model.entity.School;
import com.fsoft.fsa.kindergarten.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface IRatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByUserAndSchoolAndCriteria(User user, School school, Criteria criteria);

    @Query("SELECT new com.fsoft.fsa.kindergarten.model.dto.rating.RatingSummary(c.id, c.name, " +
            "COALESCE(AVG(r.value), 0.0), " +
            "COALESCE(COUNT(r.id), 0)) " +
            "FROM Criteria c " +
            "LEFT JOIN Rating r ON r.criteria.id = c.id " +
            "AND r.school.id = :schoolId " +
            "AND r.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY c.id, c.name")
    List<RatingSummary> findAverageRatingsBySchoolIdAndDateRange(
            @Param("schoolId") Integer schoolId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

    @Query("SELECT new com.fsoft.fsa.kindergarten.model.dto.rating.RatingSummary(c.id, c.name, " +
            "COALESCE(AVG(r.value), 0.0), " +
            "COALESCE(COUNT(r.id), 0)) " +
            "FROM Criteria c " +
            "LEFT JOIN Rating r ON r.criteria.id = c.id " +
            "AND r.school.id = :schoolId AND r.user.id = :userId " +
            "GROUP BY c.id, c.name")
    List<RatingSummary> findAverageRatingsBySchoolOfUser(
            @Param("schoolId") Integer schoolId,
            @Param("userId") Integer userId);




    @Query("SELECT new com.fsoft.fsa.kindergarten.model.dto.rating.RatingSummary(c.id, c.name, " +
            "COALESCE(AVG(r.value), 0.0), " +
            "COALESCE(COUNT(r.id), 0)) " +
            "FROM Criteria c " +
            "LEFT JOIN Rating r ON r.criteria.id = c.id " +
            "AND r.school.id = :schoolId " +
            "GROUP BY c.id, c.name")
    List<RatingSummary> findAverageRatingsBySchoolId(
            @Param("schoolId") Integer schoolId);

    @Query("SELECT AVG(r.value) as averageRatingValue FROM Rating r " +
            "WHERE r.school.id = :schoolId AND r.user.id = :userId")
    Double findAverageRatingBySchoolIdAndUserId(@Param("schoolId") Integer schoolId, @Param("userId") Integer userId);

    @Query("SELECT COUNT(DISTINCT r.user.id) FROM Rating r WHERE r.school.id = :schoolId")
    Integer countUserRating(@Param("schoolId") Integer schoolId);
}
