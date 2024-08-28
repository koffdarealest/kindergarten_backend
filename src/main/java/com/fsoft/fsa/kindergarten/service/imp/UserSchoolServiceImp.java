package com.fsoft.fsa.kindergarten.service.imp;

import com.fsoft.fsa.kindergarten.config.exception.EnrollException;
import com.fsoft.fsa.kindergarten.config.exception.ResourceAlreadyExistException;
import com.fsoft.fsa.kindergarten.config.exception.ResourceNotFoundException;
import com.fsoft.fsa.kindergarten.model.entity.School;
import com.fsoft.fsa.kindergarten.model.entity.User;
import com.fsoft.fsa.kindergarten.model.entity.UserSchool;
import com.fsoft.fsa.kindergarten.model.form.enroll.EnrollForm;
import com.fsoft.fsa.kindergarten.repository.IUserSchoolRepository;
import com.fsoft.fsa.kindergarten.service.SchoolService;
import com.fsoft.fsa.kindergarten.service.UserSchoolService;
import com.fsoft.fsa.kindergarten.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserSchoolServiceImp implements UserSchoolService {

    private final IUserSchoolRepository userSchoolRepository;
    private final UserService userService;
    private final SchoolService schoolService;
    private final JwtService jwtService;

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "userList", allEntries = true),
                    @CacheEvict(value = "parentList", allEntries = true)
            }
    )
    public void enrollToSchool(EnrollForm enrollForm) {
        User parent = userService.getUserById(enrollForm.getParentId());
        School school = schoolService.getSchoolById(enrollForm.getSchoolId());

        //check user login in system is school owner to just enroll their school
        if (jwtService.getRoleFromToken().equals("SCHOOL_OWNER")) {
            User userLogin = userService.getUserById(jwtService.getIdFromToken());
            if (userLogin.getId() != school.getSchoolOwnerId()) throw new EnrollException("No authority!");
        }

        List<UserSchool> userSchool = userSchoolRepository.findByUserId(parent.getId());
        //check parent has enrolled to any school
        if (userSchool.stream().anyMatch(us -> us.isStatus() && us.getPrimaryKey().getUserId().equals(parent.getId())))
            throw new ResourceAlreadyExistException("Parent has enroll to a school");

        userSchoolRepository.save(new UserSchool(parent, school, true));
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "userList", allEntries = true),
                    @CacheEvict(value = "parentList", allEntries = true)
            }
    )
    public void UnEnrollToSchool(Integer parentId, Integer schoolId) {
        User parent = userService.getUserById(parentId);
        School school = schoolService.getSchoolById(schoolId);

        //check user login in system is school owner to just un-enroll their school
        if (jwtService.getRoleFromToken().equals("SCHOOL_OWNER")) {
            User userLogin = userService.getUserById(jwtService.getIdFromToken());
            if (userLogin.getId() != school.getSchoolOwnerId()) throw new EnrollException("No authority!");
        }

        UserSchool userSchool = userSchoolRepository.findUserSchoolByUsersAndSchools(parent, school);
        if (userSchool == null) throw new ResourceNotFoundException("Parent not enrolled in this school");

        userSchool.setStatus(false);
        userSchoolRepository.save(userSchool);
    }
}
