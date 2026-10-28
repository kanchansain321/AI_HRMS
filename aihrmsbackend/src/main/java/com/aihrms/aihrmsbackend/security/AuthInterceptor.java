package com.aihrms.aihrmsbackend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.aihrms.aihrmsbackend.dto.response.CustomResponse;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuthInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            writeJson(response, HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
            return false;
        }

        String token = header.substring(7).trim();
        if (!jwtUtil.isValid(token)) {
            writeJson(response, HttpStatus.UNAUTHORIZED, "Invalid or expired token");
            return false;
        }

        Claims claims = jwtUtil.parse(token);
        String role = Roles.canonical(claims.get("role", String.class));
        request.setAttribute("employeeId", Long.valueOf(claims.getSubject()));
        request.setAttribute("email", claims.get("email", String.class));
        request.setAttribute("role", role);

        if (handler instanceof HandlerMethod handlerMethod) {
            RequireAdmin requireAdmin = handlerMethod.getMethodAnnotation(RequireAdmin.class);
            if (requireAdmin == null) {
                requireAdmin = handlerMethod.getBeanType().getAnnotation(RequireAdmin.class);
            }
            if (requireAdmin != null && !Roles.isAdmin(role)) {
                writeJson(response, HttpStatus.FORBIDDEN, "Admin access required");
                return false;
            }
        }

        return true;
    }

    private void writeJson(HttpServletResponse response, HttpStatus status, String message) throws Exception {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        CustomResponse body = new CustomResponse(status.value(), null, message);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}