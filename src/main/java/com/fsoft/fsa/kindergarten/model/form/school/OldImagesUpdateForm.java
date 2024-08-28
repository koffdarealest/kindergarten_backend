package com.fsoft.fsa.kindergarten.model.form.school;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OldImagesUpdateForm {
    private Integer id;
    private String imagePath;
}
