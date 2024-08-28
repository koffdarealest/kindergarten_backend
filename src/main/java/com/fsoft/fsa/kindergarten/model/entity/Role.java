package com.fsoft.fsa.kindergarten.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "m_role")
public class Role extends BaseEntity implements Serializable {

    @Column(name = "name")
    private String name;
}
