package com.fsoft.fsa.kindergarten.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@Builder
public class UserDTO implements Serializable {
    private int id;
    private String userName;
    private String fullName;
    private String email;
    private Date dob;
    private String address;
    private String phone;
    private Integer roleId;
    private String status;
}
