package com.aihrms.aihrmsbackend.service.impl;

import com.aihrms.aihrmsbackend.dto.request.EmployeeAddUpdateReq;
import com.aihrms.aihrmsbackend.dto.response.EmployeeResponse;
import com.aihrms.aihrmsbackend.entity.Designation;
import com.aihrms.aihrmsbackend.entity.EmpRole;
import com.aihrms.aihrmsbackend.entity.Employee;
import com.aihrms.aihrmsbackend.repository.EmpRoleRepo;
import com.aihrms.aihrmsbackend.repository.EmployeeRepo;
import com.aihrms.aihrmsbackend.service.EmployeeService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    private  EmployeeRepo employeeRepo;

    @Autowired
    private EmpRoleRepo empRoleRepo;

    @Override
    public EmployeeResponse createEmployee(EmployeeAddUpdateReq request) {
        try {
            if (request != null) {

                // 1. Duplicate Email check
                if (!employeeRepo.existsByEmail(request.getEmail()).isEmpty()) {
                    throw new RuntimeException("Email already registered :" + request.getEmail());

                }
                // 2.Dto-> Entity mapping
                Employee employee = new Employee();
                employee.setName(request.getName());
                employee.setEmail(request.getEmail());
                employee.setPassword(request.getPassword());
//                employee.setPassword(passwordEncoder.encode(request.getPassword())); // hashed
                employee.setDesignation(new Designation(request.getDesignation().longValue()));
                employee.setJoiningDate(request.getJoiningDate());
                employee.setActive(true);

                //3 Role set
                if (request.getRole() != null) {
                    EmpRole empRole = empRoleRepo.findById(request.getRole().longValue())
                            .orElseThrow(() -> new RuntimeException(
                                    "Role not found with id: " + request.getRole()));
                    employee.setRole(empRole);
                }
                // 4. Save
                Employee savedEmployee = employeeRepo.save(employee);

                // 6. Entity -> Response DTO
                return mapToResponse(savedEmployee);

            } else {
                return null;
            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private EmployeeResponse mapToResponse(Employee savedEmployee) {

        EmployeeResponse employeeResponse= new EmployeeResponse();
         employeeResponse.setFirstName(savedEmployee.getName());
         employeeResponse.setEmail(savedEmployee.getEmail());
         employeeResponse.setDesignation(savedEmployee.getDesignation().getDesignationName());
         employeeResponse.setDepartmentName(savedEmployee.getDepartment().getDepartmentName());
         employeeResponse.setPhoneNumber(savedEmployee.getPhoneNumber());
         employeeResponse.setStatus(savedEmployee.getStatus().toString());
         return employeeResponse;



    }

    @Override
    public EmployeeResponse findByIdEmployee(Long id) {

        try {
            Employee employee = employeeRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
            return mapToResponse(employee);

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
        }




}
