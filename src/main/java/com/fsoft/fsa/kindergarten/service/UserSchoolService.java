package com.fsoft.fsa.kindergarten.service;

import com.fsoft.fsa.kindergarten.model.form.enroll.EnrollForm;

public interface UserSchoolService {
    void enrollToSchool(EnrollForm enrollForm);

    void UnEnrollToSchool(Integer parentId, Integer schoolId);
}
