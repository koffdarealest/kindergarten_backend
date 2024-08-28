package com.fsoft.fsa.kindergarten.service;

import com.fsoft.fsa.kindergarten.model.dto.role.RoleDTO;
import com.fsoft.fsa.kindergarten.model.entity.Role;

import java.util.List;

public interface RoleService {

    List<RoleDTO> getAll();

    boolean roleExists(Integer roleId);

    Role getRoleById(int roleId);
}
