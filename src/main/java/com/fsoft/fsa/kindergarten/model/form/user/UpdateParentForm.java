package com.fsoft.fsa.kindergarten.model.form.user;

import com.fsoft.fsa.kindergarten.model.validation.user.DOBInvalid;
import com.fsoft.fsa.kindergarten.model.validation.user.PhoneNumber;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateParentForm {

    @NotBlank(message = "{field.blank}")
    private String fullName;

    private String city;

    private String district;

    private String ward;

    private String addressLine;

    @DOBInvalid
    private String dob;

    @NotBlank(message = "field.blank")
    @PhoneNumber
    private String phone;

    private String avatar;
}
