package com.aihrms.aihrmsbackend.service.impl;

import com.aihrms.aihrmsbackend.dto.response.CustomResponse;
import com.aihrms.aihrmsbackend.entity.Department;
import com.aihrms.aihrmsbackend.repository.DepartmentRepo;
import com.aihrms.aihrmsbackend.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class DepartmentServiceImp implements DepartmentService {

    @Autowired
    private DepartmentRepo departmentRepo;

    @Override
    public CustomResponse addDepartment(String name){
        try {
            Department department=new Department();
            department.setDepartmentName(name);
            department.setCreatedOn(new Date());
            department.setIsActive(true);

            departmentRepo.save(department);

            return new CustomResponse(HttpStatus.OK.value(),null,"Department Saved Successfully");
        }
        catch (Exception e) {
            e.printStackTrace();

            return new CustomResponse(HttpStatus.BAD_REQUEST.value(),null,"Something Went Wrong");
        }

    }


}
