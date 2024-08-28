package com.fsoft.fsa.kindergarten.model.form.enroll;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollForm {
    @NotNull(message = "{user.id.blank}")
    private Integer parentId;

    @NotNull(message = "{request.school.blank}")
    private Integer schoolId;

}
