package com.fsoft.fsa.kindergarten.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "t_school")
@Accessors(chain = true)
public class School extends BaseEntity implements Serializable {

    @Column(name = "name")
    private String name;

    @Column(name = "phone")
    private String phone;

    @Column(name = "introduction", length = 3000)
    private String introduction;

    @Column(name = "fee_from")
    private Double feeFrom;

    @Column(name = "fee_to")
    private Double feeTo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "m_school_age_id", referencedColumnName = "id")
    private SchoolAge age;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "m_school_type_id", referencedColumnName = "id")
    private SchoolType type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "m_school_status_id", referencedColumnName = "id")
    private SchoolStatus status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "m_school_education_method_id", referencedColumnName = "id")
    private EducationMethod method;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "school_facilities",
            joinColumns = @JoinColumn(name = "school_id"),
            inverseJoinColumns = @JoinColumn(name = "facility_id"))
    private Set<Facilities> facilities;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "school_utilities",
            joinColumns = @JoinColumn(name = "school_id"),
            inverseJoinColumns = @JoinColumn(name = "utility_id"))
    private Set<Utilities> utilities;

    @OneToMany(mappedBy = "schools",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JsonIgnore
    private List<UserSchool> userSchools;

    @Column(name = "address_line")
    private String addressLine;

    @Column(name = "address_ward")
    private String ward;

    @Column(name = "address_district")
    private String district;

    @Column(name = "address_city")
    private String city;

    @Column(name = "email")
    private String email;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "school", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<SchoolImage> images;

    @Column
    private Integer schoolOwnerId;

    public void updateImages(List<SchoolImage> images) {
        if (this.images != null) {
            this.images.clear();
            if (!images.isEmpty()) {
                this.images.addAll(images);
            }
        }
    }
}
