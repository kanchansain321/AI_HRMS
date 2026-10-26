package com.aihrms.aihrmsbackend.controller;

import com.aihrms.aihrmsbackend.dto.request.EmployeeAddUpdateReq;
import com.aihrms.aihrmsbackend.dto.response.EmployeeResponse;
import com.aihrms.aihrmsbackend.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private  EmployeeService employeeService;

    // CREATE
    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeAddUpdateReq request) {
        EmployeeResponse  response = employeeService.createEmployee(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping
    public ResponseEntity<EmployeeResponse> getEmployeeById(@RequestParam("empId")Long  empId)
    {
        EmployeeResponse  response = employeeService.findByIdEmployee(empId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @GetMapping("/all")
    public ResponseEntity<?>getAllEmployee()
    {

        List<EmployeeResponse>responseList=employeeService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(responseList);

    }

    
}
