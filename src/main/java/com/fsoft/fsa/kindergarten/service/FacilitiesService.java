package com.fsoft.fsa.kindergarten.service;

import com.fsoft.fsa.kindergarten.model.dto.school.FacilitiesDTO;
import com.fsoft.fsa.kindergarten.model.entity.Facilities;

import java.util.List;
import java.util.Set;

public interface FacilitiesService {
    List<FacilitiesDTO> getAllDTO();

    Facilities getFacilitiesById(int id);

    Set<Facilities> getFacilitiesByIds(Set<Integer> ids);
}
