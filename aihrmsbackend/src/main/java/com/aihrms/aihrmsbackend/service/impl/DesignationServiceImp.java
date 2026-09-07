package com.aihrms.aihrmsbackend.service.impl;

import com.aihrms.aihrmsbackend.dto.response.CustomResponse;
import com.aihrms.aihrmsbackend.dto.response.DesignationResponse;
import com.aihrms.aihrmsbackend.entity.Designation;
import com.aihrms.aihrmsbackend.repository.DesignationRepo;
import com.aihrms.aihrmsbackend.service.DesignationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DesignationServiceImp implements DesignationService {

    @Autowired
    private DesignationRepo designationRepo;


    // Convert Entity to Response
    private DesignationResponse mapToResponse(Designation designation) {

        DesignationResponse response = new DesignationResponse();

        response.setId(designation.getId());
        response.setDesignationName(designation.getDesignationName());
        response.setIsActive(designation.getIsActive());
        response.setCreatedOn(designation.getCreatedOn());

        return response;
    }


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

            DesignationResponse response =
                    mapToResponse(designation);


            return new CustomResponse(
                    HttpStatus.OK.value(),
                    response,
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

            Optional<Designation> optionalDesignation =
                    designationRepo.findById(id);

            if (optionalDesignation.isEmpty()) {

                return new CustomResponse(
                        HttpStatus.NOT_FOUND.value(),
                        null,
                        "Designation Not Found"
                );
            }

            Designation designation =
                    optionalDesignation.get();

            DesignationResponse response =
                    mapToResponse(designation);

            return new CustomResponse(
                    HttpStatus.OK.value(),
                    response,
                    "Designation Found Successfully"
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


    // Get All Designations
    @Override
    public CustomResponse getAllDesignations() {

        try {

            List<Designation> designations =
                    designationRepo.findAll();

            List<DesignationResponse> responseList =
                    new ArrayList<>();

            for (Designation designation : designations) {

                DesignationResponse response =
                        mapToResponse(designation);

                responseList.add(response);
            }

            return new CustomResponse(
                    HttpStatus.OK.value(),
                    responseList,
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
