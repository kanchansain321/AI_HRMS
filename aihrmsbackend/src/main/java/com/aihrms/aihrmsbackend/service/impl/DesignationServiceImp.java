package com.aihrms.aihrmsbackend.service.impl;

import com.aihrms.aihrmsbackend.dto.response.CustomResponse;
import com.aihrms.aihrmsbackend.entity.Designation;
import com.aihrms.aihrmsbackend.repository.DesignationRepo;
import com.aihrms.aihrmsbackend.service.DesignationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DesignationServiceImp implements DesignationService {

    @Autowired
    private DesignationRepo designationRepo;

    // Add Designation
    @Override
    public CustomResponse addDesignation(String name) {

        try {

            Optional<Designation> existingDesignation =
                    designationRepo.findByDesignationName(name);

            if (existingDesignation.isPresent()) {

                return new CustomResponse(
                        HttpStatus.CONFLICT.value(),
                        null,
                        "Designation Already Exists"
                );
            }
            Designation designation = new Designation();

            designation.setDesignationName(name);
            designation.setIsActive(true);

            designationRepo.save(designation);

            return new CustomResponse(
                    HttpStatus.OK.value(),
                    designation,
                    "Designation Saved Successfully"
            );

        } catch (Exception e) {

            e.printStackTrace();

            return new CustomResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    null,
                    "Something Went Wrong"
            );
        }
    }


    // Get Designation By ID
    @Override
    public CustomResponse getDesignationById(Long id) {

        try {

            Designation designation =
                    designationRepo.getById(id);

            return new CustomResponse(
                    HttpStatus.OK.value(),
                    designation,
                    "Designation Found Successfully"
            );

        } catch (Exception e) {

            e.printStackTrace();

            return new CustomResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    null,
                    "Designation Not Found"
            );
        }
    }


    // Get All Designations
    @Override
    public CustomResponse getAllDesignations() {

        try {

            List<Designation> designations =
                    designationRepo.findAll();

            return new CustomResponse(
                    HttpStatus.OK.value(),
                    designations,
                    "Designations Found Successfully"
            );

        } catch (Exception e) {

            e.printStackTrace();

            return new CustomResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    null,
                    "Something Went Wrong"
            );
        }


    }
}
