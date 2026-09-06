package com.aihrms.aihrmsbackend.controller;

import com.aihrms.aihrmsbackend.service.DesignationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/designation")
public class DesignationController {
    @Autowired
    private DesignationService designationService;


    // Add Designation
    @PostMapping
    public ResponseEntity<?> createDesignation(
            @RequestParam("designationName") String designationName) {

        return ResponseEntity.ok(
                designationService.addDesignation(designationName)
        );
    }


    // Get Designation By ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getDesignationById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                designationService.getDesignationById(id)
        );
    }


    // Get All Designations
    @GetMapping
    public ResponseEntity<?> getAllDesignations() {

        return ResponseEntity.ok(
                designationService.getAllDesignations()
        );
    }

}
