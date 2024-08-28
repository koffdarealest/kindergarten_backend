package com.fsoft.fsa.kindergarten.model.dto.school;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolImageDTO implements Serializable {
    private Integer id;
    private String imagePath;
}
