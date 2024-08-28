package com.fsoft.fsa.kindergarten.service.imp;

import com.fsoft.fsa.kindergarten.config.exception.ResourceNotFoundException;
import com.fsoft.fsa.kindergarten.model.dto.school.SchoolTypeDTO;
import com.fsoft.fsa.kindergarten.model.entity.SchoolType;
import com.fsoft.fsa.kindergarten.repository.ISchoolTypeRepository;
import com.fsoft.fsa.kindergarten.service.SchoolTypeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SchoolTypeServiceImp implements SchoolTypeService {
    private final ISchoolTypeRepository schoolTypeRepository;
    private final ModelMapper modelMapper;

    public SchoolTypeDTO convertToDTO(SchoolType schoolType) {
        return modelMapper.map(schoolType, SchoolTypeDTO.class);
    }

    public SchoolType convertToEntity(SchoolTypeDTO schoolTypeDTO) {
        return modelMapper.map(schoolTypeDTO, SchoolType.class);
    }

    public List<SchoolType> getAll() {
        return schoolTypeRepository.findAll();
    }

    @Override
    @Cacheable(value = "SchoolTypeDTO")
    public List<SchoolTypeDTO> getAllDTO() {
        return getAll().stream().map(this::convertToDTO).toList();
    }

    public SchoolType getById(int id) {
        return schoolTypeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("SchoolType not found")
        );
    }
}
