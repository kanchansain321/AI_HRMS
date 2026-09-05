package com.aihrms.aihrmsbackend.controller;

import com.aihrms.aihrmsbackend.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<?> createdDepartment(@RequestParam("departmentName") String departmentName) {
        return ResponseEntity.ok(departmentService.addDepartment(departmentName));
    }






}
