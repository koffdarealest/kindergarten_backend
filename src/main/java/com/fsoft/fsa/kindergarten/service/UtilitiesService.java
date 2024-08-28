package com.fsoft.fsa.kindergarten.service;

import com.fsoft.fsa.kindergarten.model.dto.school.UtilitiesDTO;
import com.fsoft.fsa.kindergarten.model.entity.Utilities;

import java.util.List;
import java.util.Set;

public interface UtilitiesService {
    List<UtilitiesDTO> getAllDTO();

    Utilities getUtilitiesById(int id);

    Set<Utilities> getUtilitiesByIds(Set<Integer> ids);
}
