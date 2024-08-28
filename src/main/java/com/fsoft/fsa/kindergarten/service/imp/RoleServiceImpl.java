package com.fsoft.fsa.kindergarten.service.imp;

import com.fsoft.fsa.kindergarten.config.exception.ResourceNotFoundException;
import com.fsoft.fsa.kindergarten.model.dto.role.RoleDTO;
import com.fsoft.fsa.kindergarten.model.entity.Role;
import com.fsoft.fsa.kindergarten.repository.IRoleRepository;
import com.fsoft.fsa.kindergarten.service.RoleService;
import com.fsoft.fsa.kindergarten.utils.BaseMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final IRoleRepository roleRepository;
    private final BaseMapper baseMapper;

    @Override
    public List<RoleDTO> getAll() {
        List<Role> role = roleRepository.findAll();
        return baseMapper.convertList(role, RoleDTO.class);
    }

    @Override
    public boolean roleExists(Integer roleId) {
        return roleRepository.existsById(roleId);
    }

    public Role getRoleById(int roleId) {
        return roleRepository.findById(roleId).orElseThrow(() -> new ResourceNotFoundException("Role not found"));
    }

}
