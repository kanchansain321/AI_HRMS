package com.aihrms.aihrmsbackend.controller;

import com.aihrms.aihrmsbackend.security.RequireAdmin;
import com.aihrms.aihrmsbackend.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @PostMapping
    @RequireAdmin
    public ResponseEntity<?> createdDepartment(@RequestParam("departmentName") String departmentName) {
        return ResponseEntity.ok(departmentService.addDepartment(departmentName));
    }



    // Get Department By ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getDepartmentById(@PathVariable Long id) {

        return ResponseEntity.ok(
                departmentService.getDepartmentById(id)
        );
    }
    // Get All Departments
    @GetMapping
    public ResponseEntity<?> getAllDepartments() {

        return ResponseEntity.ok(
                departmentService.getAllDepartments()
        );
    }





}
