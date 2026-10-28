package com.aihrms.aihrmsbackend.config;

import com.aihrms.aihrmsbackend.entity.EmpRole;
import com.aihrms.aihrmsbackend.repository.EmpRoleRepo;
import com.aihrms.aihrmsbackend.security.Roles;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class RoleSeeder implements ApplicationRunner {

    private final EmpRoleRepo empRoleRepo;

    public RoleSeeder(EmpRoleRepo empRoleRepo) {
        this.empRoleRepo = empRoleRepo;
    }

    @Override
    public void run(ApplicationArguments args) {
        ensure(Roles.ADMIN);
        ensure(Roles.EMPLOYEE);
    }

    private void ensure(String name) {
        if (empRoleRepo.findFirstByNameIgnoreCase(name).isPresent()) {
            return;
        }
        EmpRole role = new EmpRole();
        role.setName(name);
        role.setActive(true);
        role.setCreatedOn(new Date());
        empRoleRepo.save(role);
    }
}