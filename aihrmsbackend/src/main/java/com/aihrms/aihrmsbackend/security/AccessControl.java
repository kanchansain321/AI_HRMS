package com.aihrms.aihrmsbackend.security;

import com.aihrms.aihrmsbackend.dto.response.CustomResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;

public final class AccessControl {

    private AccessControl() {
    }

    public static Long currentEmployeeId(HttpServletRequest request) {
        Object id = request.getAttribute("employeeId");
        if (id == null) {
            return null;
        }
        return Long.valueOf(id.toString());
    }

    public static String currentRole(HttpServletRequest request) {
        Object role = request.getAttribute("role");
        return role != null ? role.toString() : null;
    }

    public static boolean isAdmin(HttpServletRequest request) {
        return Roles.isAdmin(currentRole(request));
    }

    public static boolean canAccessEmployee(HttpServletRequest request, Long targetEmployeeId) {
        if (isAdmin(request)) {
            return true;
        }
        Long self = currentEmployeeId(request);
        return self != null && targetEmployeeId != null && self.equals(targetEmployeeId);
    }

    public static CustomResponse forbidden(String message) {
        return new CustomResponse(HttpStatus.FORBIDDEN.value(), null, message);
    }
}