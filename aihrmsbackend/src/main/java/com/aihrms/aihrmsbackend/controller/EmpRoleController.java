package com.aihrms.aihrmsbackend.controller;

import com.aihrms.aihrmsbackend.entity.EmpRole;
import com.aihrms.aihrmsbackend.repository.EmpRoleRepo;
import com.aihrms.aihrmsbackend.security.RequireAdmin;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/roles")
@CrossOrigin
@RequireAdmin
public class EmpRoleController {

    private final EmpRoleRepo empRoleRepo;

    public EmpRoleController(EmpRoleRepo empRoleRepo) {
        this.empRoleRepo = empRoleRepo;
    }

    @GetMapping
    public ResponseEntity<List<EmpRole>> listRoles() {
        return ResponseEntity.ok(empRoleRepo.findAll());
    }
}