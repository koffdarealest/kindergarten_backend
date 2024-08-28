package com.fsoft.fsa.kindergarten.model.form.school;

import com.fsoft.fsa.kindergarten.model.validation.user.PhoneNumber;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolForm {

    @NotBlank(message = "{school.field.blank}")
    private String name;

    @NotNull(message = "{school.field.blank}")
    private Integer type;

    @NotBlank(message = "{school.field.blank}")
    private String addressLine;

    @NotBlank(message = "{school.field.blank}")
    private String ward;

    @NotBlank(message = "{school.field.blank}")
    private String district;

    @NotBlank(message = "{school.field.blank}")
    private String city;

    @Email
    @NotBlank(message = "{school.field.blank}")
    private String email;

    @NotBlank(message = "{school.field.blank}")
    @PhoneNumber
    private String phone;

    @NotNull(message = "{school.field.blank}")
    private Integer age;

    @NotNull(message = "{school.field.blank}")
    private Integer method;

    @NotNull(message = "{school.field.blank}")
    private Double feeFrom;

    @NotNull(message = "{school.field.blank}")
    private Double feeTo;

    private Set<Integer> facilities;

    private Set<Integer> utilities;

    private String introduction;

    private Set<String> images;
}
