package com.intelliblood.matching;

import com.intelliblood.donor.Donor;

public class EligibilityChecker {

    private static final int MIN_DAYS = 90;
    private static final int MIN_AGE = 18;
    private static final int MAX_AGE = 65;
    private static final int MIN_WEIGHT = 45;

    public static boolean check(Donor d) {
        return d.isAvailable()
                && d.getDaysSinceLastDonation() >= MIN_DAYS
                && d.getAge() >= MIN_AGE
                && d.getAge() <= MAX_AGE
                && d.getWeight() >= MIN_WEIGHT;
    }

    public static String getFailReason(Donor d) {
        if (!d.isAvailable()) return "Marked unavailable";
        if (d.getDaysSinceLastDonation() < MIN_DAYS) return "Donated less than 90 days ago";
        if (d.getAge() < MIN_AGE) return "Age below 18";
        if (d.getAge() > MAX_AGE) return "Age above 65";
        if (d.getWeight() < MIN_WEIGHT) return "Weight below 45kg";
        return "Unknown";
    }
}
