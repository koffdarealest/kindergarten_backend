package com.fsoft.fsa.kindergarten.model.dto.school;

import lombok.*;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SchoolDetailDTO implements Serializable {
    private Integer id;
    private String name;
    private SchoolTypeDTO type;
    private String addressLine;
    private String ward;
    private String district;
    private String city;
    private String email;
    private String phone;
    private SchoolAgeDTO age;
    private EducationMethodDTO method;
    private Double feeFrom;
    private Double feeTo;
    private Set<FacilitiesDTO> facilities;
    private Set<UtilitiesDTO> utilities;
    private SchoolStatusDTO status;
    private String introduction;
    private List<SchoolImageDTO> images;
    private double averageRating;
    private Integer totalRating;
}
