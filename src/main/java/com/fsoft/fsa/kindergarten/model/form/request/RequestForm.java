package com.fsoft.fsa.kindergarten.model.form.request;

import com.fsoft.fsa.kindergarten.model.validation.user.PhoneNumber;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestForm {

    @NotBlank(message = "{user.fullname.blank}")
    private String fullName;

    @Email
    @NotBlank(message = "{user.email.blank}")
    private String email;

    @PhoneNumber
    @NotBlank(message = "{user.password.phone}")
    private String phone;

    @Size(max = 4000, message = "{request.inquiry.size}")
    private String question;

    @NotNull(message = "{request.school.blank}")
    private Integer schoolId;
}
