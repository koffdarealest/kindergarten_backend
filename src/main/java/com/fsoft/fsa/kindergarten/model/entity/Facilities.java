package com.fsoft.fsa.kindergarten.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "m_facilities")
public class Facilities extends BaseEntity implements Serializable {

    @Column(name = "facility_name")
    private String name;
}
