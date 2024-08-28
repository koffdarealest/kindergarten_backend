package com.fsoft.fsa.kindergarten.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class UserSchoolId implements Serializable {
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "school_id")
    private Integer schoolId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserSchoolId that = (UserSchoolId) o;

        if (!userId.equals(that.userId)) return false;
        return schoolId.equals(that.schoolId);
    }

    @Override
    public int hashCode() {
        int result = userId.hashCode();
        result = 31 * result + schoolId.hashCode();
        return result;
    }

}
