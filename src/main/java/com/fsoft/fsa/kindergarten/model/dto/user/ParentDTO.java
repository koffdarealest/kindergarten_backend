package com.fsoft.fsa.kindergarten.model.dto.user;

import com.fsoft.fsa.kindergarten.model.dto.school.SchoolRatingDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ParentDTO implements Serializable {
    private Integer id;
    private String fullName;
    private String email;
    private Date dob;
    private String phone;
    private String address;
    private Integer roleId;
    private Boolean status;
    private List<SchoolRatingDTO> schools;
    private String avatar;
}
