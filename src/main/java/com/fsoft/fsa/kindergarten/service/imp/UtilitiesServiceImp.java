package com.fsoft.fsa.kindergarten.service.imp;

import com.fsoft.fsa.kindergarten.config.exception.ResourceNotFoundException;
import com.fsoft.fsa.kindergarten.model.dto.school.UtilitiesDTO;
import com.fsoft.fsa.kindergarten.model.entity.Utilities;
import com.fsoft.fsa.kindergarten.repository.IUtilitiesRepository;
import com.fsoft.fsa.kindergarten.service.UtilitiesService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UtilitiesServiceImp implements UtilitiesService {

    private final IUtilitiesRepository utilitiesRepository;
    private final ModelMapper modelMapper;

    public UtilitiesDTO convertToDTO(Utilities utilities) {
        return modelMapper.map(utilities, UtilitiesDTO.class);
    }

    public Utilities convertToEntity(UtilitiesDTO utilitiesDTO) {
        return modelMapper.map(utilitiesDTO, Utilities.class);
    }

    public List<Utilities> getAll() {
        return utilitiesRepository.findAll();
    }

    @Override
    @Cacheable(value = "UtilitiesDTO")
    public List<UtilitiesDTO> getAllDTO() {
        return getAll().stream().map(this::convertToDTO).toList();
    }

    public Utilities getUtilitiesById(int id) {
        return utilitiesRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Utilities not found with id: " + id)
        );
    }

    public Set<Utilities> getUtilitiesByIds(Set<Integer> ids) {
        Set<Utilities> utilities = new HashSet<>();
        for (int id : ids) {
            utilities.add(getUtilitiesById(id));
        }
        return utilities;
    }
}
