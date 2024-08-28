package com.fsoft.fsa.kindergarten.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_school")
public class UserSchool implements Serializable {

    @EmbeddedId
    private UserSchoolId primaryKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    private User users;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("schoolId")
    private School schools;

    @Column(name = "status")
    private boolean status;

    public UserSchool(User user, School school, boolean status) {
        this.primaryKey = new UserSchoolId(user.getId(), school.getId());
        this.users = user;
        this.schools = school;
        this.status = status;
    }
}
