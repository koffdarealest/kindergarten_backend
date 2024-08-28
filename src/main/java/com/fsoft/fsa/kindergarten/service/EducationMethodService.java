package com.fsoft.fsa.kindergarten.service;

import com.fsoft.fsa.kindergarten.model.dto.school.EducationMethodDTO;
import com.fsoft.fsa.kindergarten.model.entity.EducationMethod;

import java.util.List;

public interface EducationMethodService {
    List<EducationMethodDTO> getAllDTO();

    EducationMethod getById(int id);
}
