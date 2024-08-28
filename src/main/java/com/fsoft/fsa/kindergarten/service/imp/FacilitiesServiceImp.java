package com.fsoft.fsa.kindergarten.service.imp;

import com.fsoft.fsa.kindergarten.config.exception.ResourceNotFoundException;
import com.fsoft.fsa.kindergarten.model.dto.school.FacilitiesDTO;
import com.fsoft.fsa.kindergarten.model.entity.Facilities;
import com.fsoft.fsa.kindergarten.repository.IFacilitiesRepository;
import com.fsoft.fsa.kindergarten.service.FacilitiesService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FacilitiesServiceImp implements FacilitiesService {

    private final IFacilitiesRepository facilitiesRepository;
    private final ModelMapper modelMapper;

    public FacilitiesDTO convertToDTO(Facilities facilities) {
        return modelMapper.map(facilities, FacilitiesDTO.class);
    }

    public Facilities convertToEntity(FacilitiesDTO facilitiesDTO) {
        return modelMapper.map(facilitiesDTO, Facilities.class);
    }

    public List<Facilities> getAll() {
        return facilitiesRepository.findAll();
    }

    @Override
    @Cacheable(value = "facilitiesDTO")
    public List<FacilitiesDTO> getAllDTO() {
        return getAll().stream().map(this::convertToDTO).toList();
    }

    public Facilities getFacilitiesById(int id) {
        return facilitiesRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Facilities not found with id: " + id)
        );
    }

    public Set<Facilities> getFacilitiesByIds(Set<Integer> ids) {
        Set<Facilities> facilities = new HashSet<>();
        for (int id : ids) {
            facilities.add(getFacilitiesById(id));
        }
        return facilities;
    }


}
