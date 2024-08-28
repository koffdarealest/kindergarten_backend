package com.fsoft.fsa.kindergarten.utils;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BaseMapper {

    private final ModelMapper modelMapper;

    public <D, T> D convert(T entity, Class<D> outClass) {
        return modelMapper.map(entity, outClass);
    }

    public <D, T> List<D> convertList(List<T> entity, Class<D> outClass) {
        return entity.stream().map(e -> convert(e, outClass)).toList();
    }
}
