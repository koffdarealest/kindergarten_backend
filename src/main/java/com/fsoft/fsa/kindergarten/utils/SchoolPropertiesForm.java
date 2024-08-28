package com.fsoft.fsa.kindergarten.utils;

import com.fsoft.fsa.kindergarten.model.entity.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SchoolPropertiesForm {
    private SchoolAge schoolAge;
    private SchoolType schoolType;
    private EducationMethod educationMethod;
    private Set<Facilities> facilities;
    private Set<Utilities> utilities;
}
