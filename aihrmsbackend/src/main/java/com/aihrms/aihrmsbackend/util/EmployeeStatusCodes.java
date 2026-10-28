package com.aihrms.aihrmsbackend.util;

/**
 * Maps UI status labels to employee.status_id (and back).
 * 1 = ACTIVE, 0 = INACTIVE.
 */
public final class EmployeeStatusCodes {

    public static final int ACTIVE = 1;
    public static final int INACTIVE = 0;

    private EmployeeStatusCodes() {
    }

    public static Integer toId(String status) {
        if (status == null || status.isBlank()) {
            return ACTIVE;
        }
        String s = status.trim();
        if (s.chars().allMatch(Character::isDigit)) {
            return Integer.valueOf(s);
        }
        String u = s.toUpperCase().replace(' ', '_');
        if (u.equals("INACTIVE") || u.equals("DISABLED") || u.equals("FALSE")) {
            return INACTIVE;
        }
        return ACTIVE;
    }

    public static String toLabel(Integer statusId) {
        if (statusId == null) {
            return null;
        }
        if (statusId == INACTIVE) {
            return "INACTIVE";
        }
        if (statusId == ACTIVE) {
            return "ACTIVE";
        }
        return String.valueOf(statusId);
    }
}