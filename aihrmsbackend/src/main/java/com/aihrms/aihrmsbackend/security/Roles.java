package com.aihrms.aihrmsbackend.security;

public final class Roles {

    public static final String SUPER_ADMIN = "SUPER_ADMIN";
    public static final String ADMIN = "ADMIN";
    public static final String HR = "HR";
    public static final String EMPLOYEE = "EMPLOYEE";

    private Roles() {
    }

    /** Canonical JWT / UI role name. */
    public static String canonical(String role) {
        if (role == null || role.isBlank()) {
            return EMPLOYEE;
        }
        String u = role.trim().toUpperCase().replace(' ', '_').replace('-', '_');
        if (u.equals("SUPER_ADMIN") || u.equals("SUPERADMIN")) {
            return SUPER_ADMIN;
        }
        if (u.equals("HR") || u.equals("HR_ADMIN")) {
            return HR;
        }
        if (u.contains("ADMIN")) {
            return ADMIN;
        }
        return EMPLOYEE;
    }

    public static boolean isAdmin(String role) {
        String c = canonical(role);
        return SUPER_ADMIN.equals(c) || ADMIN.equals(c) || HR.equals(c);
    }
}