package com.fsoft.fsa.kindergarten.model.dto.school;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class SchoolTypeDTO implements Serializable {
    private int id;
    private String type;
}
