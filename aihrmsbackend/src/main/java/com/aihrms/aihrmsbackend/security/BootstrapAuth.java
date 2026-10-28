package com.aihrms.aihrmsbackend.security;

/**
 * Virtual SUPER_ADMIN login used only while the employee table is empty.
 * Credentials come from auth.bootstrap.* (env-overridable).
 */
public final class BootstrapAuth {

    public static final long EMPLOYEE_ID = 0L;
    public static final String ROLE = "SUPER_ADMIN";

    private BootstrapAuth() {
    }

    public static boolean matches(
            long employeeCount,
            String email,
            String password,
            String configuredEmail,
            String configuredPassword) {
        if (employeeCount > 0) {
            return false;
        }
        if (email == null || password == null || configuredEmail == null || configuredPassword == null) {
            return false;
        }
        String e = email.trim();
        String cfg = configuredEmail.trim();
        if (e.isEmpty() || cfg.isEmpty() || configuredPassword.isEmpty()) {
            return false;
        }
        return cfg.equalsIgnoreCase(e) && configuredPassword.equals(password);
    }
}