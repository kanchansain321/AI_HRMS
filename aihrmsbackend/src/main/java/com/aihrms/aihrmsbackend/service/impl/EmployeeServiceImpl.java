package com.aihrms.aihrmsbackend.service.impl;

import com.aihrms.aihrmsbackend.dto.request.EmployeeAddUpdateReq;
import com.aihrms.aihrmsbackend.dto.response.EmployeeResponse;
import com.aihrms.aihrmsbackend.entity.Department;
import com.aihrms.aihrmsbackend.entity.Designation;
import com.aihrms.aihrmsbackend.entity.EmpRole;
import com.aihrms.aihrmsbackend.entity.Employee;
import com.aihrms.aihrmsbackend.repository.DepartmentRepo;
import com.aihrms.aihrmsbackend.repository.DesignationRepo;
import com.aihrms.aihrmsbackend.repository.EmpRoleRepo;
import com.aihrms.aihrmsbackend.repository.EmployeeRepo;
import com.aihrms.aihrmsbackend.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    private  EmployeeRepo employeeRepo;

    @Autowired
    private EmpRoleRepo empRoleRepo;

    @Autowired
    private DesignationRepo designationRepo;

    @Autowired
    private DepartmentRepo departmentRepo;

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
                if(request.getDepartment()!=null){
                    Optional<Department> optionalDepartment= departmentRepo.findById(Long.valueOf(request.getDepartment()));
                    if(optionalDepartment.isPresent()){
                        employee.setDepartment(optionalDepartment.get());
                    }
                }

                //Designation
                if(request.getDesignation()!=null)
                {
                    Optional<Designation>optionalDesignation= designationRepo.findById(Long.valueOf(request.getDesignation()));
                    if(optionalDesignation.isPresent())
                    {
                        employee.setDesignation(optionalDesignation.get());
                    }
                }



//                employee.setPassword(passwordEncoder.encode(request.getPassword())); // hashed
//                employee.setDesignation(new Designation(request.getDesignation().longValue()));
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
        employeeResponse.setId(savedEmployee.getId());
         employeeResponse.setFirstName(savedEmployee.getName());
         employeeResponse.setEmail(savedEmployee.getEmail());
         employeeResponse.setDesignation(savedEmployee.getDesignation().getDesignationName());
         employeeResponse.setDepartmentName(savedEmployee.getDepartment().getDepartmentName());
         employeeResponse.setPhoneNumber(savedEmployee.getPhoneNumber());
         employeeResponse.setStatus(String.valueOf(savedEmployee.getStatus()));
         employeeResponse.setJoiningDate(savedEmployee.getJoiningDate());
         employeeResponse.setPhoneNumber(savedEmployee.getPhoneNumber());
         employeeResponse.setUpdatedAt(savedEmployee.getUpdatedAt());


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

        //GetAll
    @Override
        public List<EmployeeResponse> findAll() {

        try {
            List<Employee> employees = employeeRepo.findAll();

            if(employees!=null && !employees.isEmpty()) {

                List<EmployeeResponse> responseList = new ArrayList<>();

                for (Employee employee : employees) {
                    EmployeeResponse response = mapToResponse(employee);
                    responseList.add(response);
                }

                return responseList;
            }else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ─── UPDATE EMPLOYEE ────────────────────────────────────────
    @Override
    public EmployeeResponse updateEmployee(EmployeeAddUpdateReq request) {
        try {
            // 1. Find the existing employee (throws error if not found)
            Employee employee = employeeRepo.findById(Long.valueOf(request.getId()))
                    .orElseThrow(() -> new RuntimeException("Employee not found with id: " + request.getId()));

            // 2. Update basic fields (only if provided in request)
            if (request.getName() != null) {
                employee.setName(request.getName());
            }
            if (request.getEmail() != null) {
                // Check if new email is already taken by ANOTHER employee
                List<Employee> emailCheck = employeeRepo.existsByEmail(request.getEmail());
                if (!emailCheck.isEmpty() && !emailCheck.get(0).getId().equals(request.getId())) {
                    throw new RuntimeException("Email already registered: " + request.getEmail());
                }
                employee.setEmail(request.getEmail());
            }
            if (request.getPhoneNumber() != null) {
                employee.setPhoneNumber(request.getPhoneNumber());
            }
            if (request.getJoiningDate() != null) {
                employee.setJoiningDate(request.getJoiningDate());
            }

            // 3. Update Department (if department ID is provided)
            if (request.getDepartment() != null) {
                Department department = departmentRepo.findById(Long.valueOf(request.getDepartment()))
                        .orElseThrow(() -> new RuntimeException(
                                "Department not found with id: " + request.getDepartment()));
                employee.setDepartment(department);
            }

            // 4. Update Designation (if designation ID is provided)
            if (request.getDesignation() != null) {
                Designation designation = designationRepo.findById(Long.valueOf(request.getDesignation()))
                        .orElseThrow(() -> new RuntimeException(
                                "Designation not found with id: " + request.getDesignation()));
                employee.setDesignation(designation);
            }

            // 5. Update Role (if role ID is provided)
            if (request.getRole() != null) {
                EmpRole empRole = empRoleRepo.findById(request.getRole().longValue())
                        .orElseThrow(() -> new RuntimeException(
                                "Role not found with id: " + request.getRole()));
                employee.setRole(empRole);
            }

            // 6. Set the updated timestamp
            employee.setUpdatedAt(new java.util.Date());

            // 7. Save updated employee to database
            Employee savedEmployee = employeeRepo.save(employee);

            // 8. Convert entity to response DTO and return
            return mapToResponse(savedEmployee);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }





}
