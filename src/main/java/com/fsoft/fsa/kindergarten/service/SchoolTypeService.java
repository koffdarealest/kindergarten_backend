package com.fsoft.fsa.kindergarten.service;

import com.fsoft.fsa.kindergarten.model.dto.school.SchoolTypeDTO;
import com.fsoft.fsa.kindergarten.model.entity.SchoolType;

import java.util.List;

public interface SchoolTypeService {
    List<SchoolTypeDTO> getAllDTO();

    SchoolType getById(int id);
}
