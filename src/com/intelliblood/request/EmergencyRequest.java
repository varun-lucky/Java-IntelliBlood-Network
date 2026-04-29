package com.intelliblood.request;

import com.intelliblood.donor.InvalidBloodGroupException;

public class EmergencyRequest {

    private String hospitalName;
    private String bloodGroup;
    private String city;
    private String urgencyLevel;
    private String patientName;

    public static final String[] VALID_GROUPS = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

    public EmergencyRequest(String hospitalName, String bloodGroup, String city,
                            String urgencyLevel, String patientName) throws InvalidBloodGroupException {
        if (!isValidBloodGroup(bloodGroup)) {
            throw new InvalidBloodGroupException("Invalid blood group: " + bloodGroup +
                    ". Valid groups: A+, A-, B+, B-, AB+, AB-, O+, O-");
        }
        this.hospitalName = hospitalName;
        this.bloodGroup = bloodGroup.toUpperCase();
        this.city = city;
        this.urgencyLevel = urgencyLevel;
        this.patientName = patientName;
    }

    private boolean isValidBloodGroup(String bg) {
        for (String g : VALID_GROUPS) {
            if (g.equalsIgnoreCase(bg)) return true;
        }
        return false;
    }

    public static String[] getValidGroups() { return VALID_GROUPS; }
    public String getBloodGroup() { return bloodGroup; }
    public String getCity() { return city; }
    public String getHospitalName() { return hospitalName; }
    public String getUrgencyLevel() { return urgencyLevel; }
    public String getPatientName() { return patientName; }

    public String toString() {
        return "Hospital: " + hospitalName + " | Patient: " + patientName +
               " | Blood: " + bloodGroup + " | City: " + city + " | Urgency: " + urgencyLevel;
    }
}
