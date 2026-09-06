package com.aihrms.aihrmsbackend.service.impl;

import com.aihrms.aihrmsbackend.dto.response.CustomResponse;
import com.aihrms.aihrmsbackend.dto.response.DepartmentResponse;
import com.aihrms.aihrmsbackend.entity.Department;
import com.aihrms.aihrmsbackend.repository.DepartmentRepo;
import com.aihrms.aihrmsbackend.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class DepartmentServiceImp implements DepartmentService {

    @Autowired
    private DepartmentRepo departmentRepo;

    @Override
    public CustomResponse addDepartment(String name){

        try {

            // Duplicate name check
            Optional<Department> existingDepartment =
                    departmentRepo.findByDepartmentName(name);

            if (existingDepartment.isPresent()) {

                return new CustomResponse(
                        HttpStatus.CONFLICT.value(),
                        null,
                        "Department Already Exists"
                );
            }


            Department department = new Department();

            department.setDepartmentName(name);
            department.setCreatedOn(new Date());
            department.setIsActive(true);

            departmentRepo.save(department);
            // Entity -> Response
            DepartmentResponse response = mapToResponse(department);


            return new CustomResponse(
                    HttpStatus.OK.value(),
                    department,
                    "Department Saved Successfully"
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


    // Get Department By ID
    @Override
    public CustomResponse getDepartmentById(Long id) {

        try {

            Optional<Department> department =
                    departmentRepo.findById(id);
            //Department department = departmentRepo.getById(id);

            if (department.isEmpty()) {

                return new CustomResponse(
                        HttpStatus.NOT_FOUND.value(),
                        null,
                        "Department Not Found"
                );
            }

            // Entity -> Response
            DepartmentResponse response =
                    mapToResponse(department.get());

            return new CustomResponse(
                    HttpStatus.OK.value(),
                    department.get(),
                    "Department Found Successfully"
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


    // Get All Departments
    @Override
    public CustomResponse getAllDepartments() {

        try {

            List<Department> departments =
                    departmentRepo.findAll();

            List<DepartmentResponse> responseList =
                    new ArrayList<>();


            for (Department department : departments) {

                DepartmentResponse response =
                        mapToResponse(department);

                responseList.add(response);
            }

            return new CustomResponse(
                    HttpStatus.OK.value(),
                    departments,
                    "Departments Found Successfully"
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

    // Entity -> Response
    private DepartmentResponse mapToResponse(Department department) {

        DepartmentResponse response =
                new DepartmentResponse();

        response.setId(department.getId());
        response.setDepartmentName(
                department.getDepartmentName()
        );
        response.setActive(
                department.getIsActive()
        );

        return response;
    }


}
