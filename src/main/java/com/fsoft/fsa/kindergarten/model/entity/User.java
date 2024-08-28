package com.fsoft.fsa.kindergarten.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "t_user")
public class User extends BaseEntity {

    @Column(name = "username", unique = true)
    private String userName;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "password")
    private String password;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "date_of_birth")
    private Date dob;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address_line")
    private String addressLine;

    @Column(name = "address_ward")
    private String ward;

    @Column(name = "address_district")
    private String district;

    @Column(name = "address_city")
    private String city;

    @Column(name = "status")
    private String status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Role role;

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private Set<Rating> ratings = new HashSet<>();

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<UserSchool> userSchools;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @PrePersist
    protected void onCreate() {
        isDeleted = false;
    }
}
