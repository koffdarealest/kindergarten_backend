package com.fsoft.fsa.kindergarten.service;

import com.fsoft.fsa.kindergarten.model.dto.school.SchoolStatusDTO;
import com.fsoft.fsa.kindergarten.model.entity.SchoolStatus;

import java.util.List;

public interface SchoolStatusService {
    List<SchoolStatusDTO> getAllDTO();

    SchoolStatus getStatusById(int id);

    List<SchoolStatus> getAll();
}
