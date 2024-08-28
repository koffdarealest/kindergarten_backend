package com.fsoft.fsa.kindergarten.service;

import com.fsoft.fsa.kindergarten.model.dto.school.SchoolAgeDTO;
import com.fsoft.fsa.kindergarten.model.entity.SchoolAge;

import java.util.List;

public interface SchoolAgeService {
    List<SchoolAgeDTO> getAllDTO();

    SchoolAge getById(int id);
}
