package com.fsoft.fsa.kindergarten.service.imp;

import com.fsoft.fsa.kindergarten.config.exception.ResourceNotFoundException;
import com.fsoft.fsa.kindergarten.model.dto.school.SchoolStatusDTO;
import com.fsoft.fsa.kindergarten.model.entity.SchoolStatus;
import com.fsoft.fsa.kindergarten.repository.ISchoolStatusRepository;
import com.fsoft.fsa.kindergarten.service.SchoolStatusService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SchoolStatusServiceImp implements SchoolStatusService {
    private final ISchoolStatusRepository schoolStatusRepository;
    private final ModelMapper modelMapper;

    public SchoolStatusDTO convertToDTO(SchoolStatus schoolStatus) {
        return modelMapper.map(schoolStatus, SchoolStatusDTO.class);
    }

    public SchoolStatus convertToEntity(SchoolStatusDTO schoolStatusDTO) {
        return modelMapper.map(schoolStatusDTO, SchoolStatus.class);
    }

    public List<SchoolStatus> getAll() {
        return schoolStatusRepository.findAll();
    }

    @Override
    @Cacheable(value = "schoolStatusDTO")
    public List<SchoolStatusDTO> getAllDTO() {
        return getAll().stream().map(this::convertToDTO).toList();
    }

    public SchoolStatus getStatusById(int id) {
        return schoolStatusRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Status not found")
        );
    }
}
