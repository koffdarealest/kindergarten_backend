package com.fsoft.fsa.kindergarten.service.imp;

import com.fsoft.fsa.kindergarten.config.exception.ResourceNotFoundException;
import com.fsoft.fsa.kindergarten.model.dto.school.SchoolAgeDTO;
import com.fsoft.fsa.kindergarten.model.entity.SchoolAge;
import com.fsoft.fsa.kindergarten.repository.ISchoolAgeRepository;
import com.fsoft.fsa.kindergarten.service.SchoolAgeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SchoolAgeServiceImp implements SchoolAgeService {
    private final ISchoolAgeRepository schoolAgeRepository;
    private final ModelMapper modelMapper;

    public SchoolAgeDTO convertToDTO(SchoolAge schoolAge) {
        return modelMapper.map(schoolAge, SchoolAgeDTO.class);
    }

    public SchoolAge convertToEntity(SchoolAgeDTO SchoolAgeDTO) {
        return modelMapper.map(SchoolAgeDTO, SchoolAge.class);
    }

    public List<SchoolAge> getAll() {
        return schoolAgeRepository.findAll();
    }

    @Override
    @Cacheable(value = "SchoolAgeDTO")
    public List<SchoolAgeDTO> getAllDTO() {
        return getAll().stream().map(this::convertToDTO).toList();
    }

    public SchoolAge getById(int id) {
        return schoolAgeRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("SchoolAge not found")
        );
    }



}
