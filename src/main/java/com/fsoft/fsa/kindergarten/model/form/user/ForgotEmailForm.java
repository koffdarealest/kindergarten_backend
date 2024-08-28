package com.fsoft.fsa.kindergarten.model.form.user;

import com.fsoft.fsa.kindergarten.model.validation.user.CustomEmailAnnotation;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ForgotEmailForm {
    @CustomEmailAnnotation // Include Not blank, email valid and User email exist
    private String email;
}
