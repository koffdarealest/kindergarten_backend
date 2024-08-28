package com.fsoft.fsa.kindergarten.service.imp;

import com.fsoft.fsa.kindergarten.config.exception.ResourceNotFoundException;
import com.fsoft.fsa.kindergarten.model.dto.school.EducationMethodDTO;
import com.fsoft.fsa.kindergarten.model.entity.EducationMethod;
import com.fsoft.fsa.kindergarten.repository.IEducationMethodRepository;
import com.fsoft.fsa.kindergarten.service.EducationMethodService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EducationMethodServiceImp implements EducationMethodService {

    private final IEducationMethodRepository educationMethodRepository;
    private final ModelMapper modelMapper;

    public EducationMethodDTO convertToDTO(EducationMethod method) {
        return modelMapper.map(method, EducationMethodDTO.class);
    }

    public EducationMethod convertToEntity(EducationMethodDTO methodDTO) {
        return modelMapper.map(methodDTO, EducationMethod.class);
    }

    public List<EducationMethod> getAll() {
        return educationMethodRepository.findAll();
    }

    @Override
    @Cacheable(value = "EducationMethodDTO")
    public List<EducationMethodDTO> getAllDTO() {
        return getAll().stream().map(this::convertToDTO).toList();
    }

    public EducationMethod getById(int id) {
        return educationMethodRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("EducationMethod not found")
        );
    }
}
