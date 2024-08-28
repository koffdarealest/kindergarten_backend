package com.fsoft.fsa.kindergarten.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ParentDetailDTO implements Serializable {
    private Integer id;
    private String fullName;
    private String email;
    private String addressLine;
    private String ward;
    private String district;
    private String city;
    private Date dob;
    private String phone;
    private String avatar;
}
