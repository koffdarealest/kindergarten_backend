package com.fsoft.fsa.kindergarten.controller;

import com.fsoft.fsa.kindergarten.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/role")
@Tag(name = "Role Controller")
@RequiredArgsConstructor
@CrossOrigin
public class RoleController {
    private final RoleService roleService;

    @Operation(summary = "Get role list", description = "API get role list")
    @GetMapping(path = "/list")
    public List<?> getListRole() {
        return roleService.getAll();
    }

}
