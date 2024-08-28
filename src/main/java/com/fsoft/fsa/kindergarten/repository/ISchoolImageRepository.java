package com.fsoft.fsa.kindergarten.repository;

import com.fsoft.fsa.kindergarten.model.entity.SchoolImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ISchoolImageRepository extends JpaRepository<SchoolImage, Integer>{
    List<SchoolImage> findSchoolImagesBySchoolId(int schoolId);
}
