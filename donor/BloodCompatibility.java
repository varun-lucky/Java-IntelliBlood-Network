package com.intelliblood.donor;

import java.util.ArrayList;
import java.util.HashMap;

public class BloodCompatibility {

    private static final HashMap<String, ArrayList<String>> compatibleDonors = new HashMap<>();

    static {
        ArrayList<String> aMinus = new ArrayList<>();
        aMinus.add("A-"); aMinus.add("O-");

        ArrayList<String> aPlus = new ArrayList<>();
        aPlus.add("A+"); aPlus.add("A-"); aPlus.add("O+"); aPlus.add("O-");

        ArrayList<String> bMinus = new ArrayList<>();
        bMinus.add("B-"); bMinus.add("O-");

        ArrayList<String> bPlus = new ArrayList<>();
        bPlus.add("B+"); bPlus.add("B-"); bPlus.add("O+"); bPlus.add("O-");

        ArrayList<String> abMinus = new ArrayList<>();
        abMinus.add("AB-"); abMinus.add("A-"); abMinus.add("B-"); abMinus.add("O-");

        ArrayList<String> abPlus = new ArrayList<>();
        abPlus.add("A+"); abPlus.add("A-"); abPlus.add("B+"); abPlus.add("B-");
        abPlus.add("AB+"); abPlus.add("AB-"); abPlus.add("O+"); abPlus.add("O-");

        ArrayList<String> oMinus = new ArrayList<>();
        oMinus.add("O-");

        ArrayList<String> oPlus = new ArrayList<>();
        oPlus.add("O+"); oPlus.add("O-");

        compatibleDonors.put("A-", aMinus);
        compatibleDonors.put("A+", aPlus);
        compatibleDonors.put("B-", bMinus);
        compatibleDonors.put("B+", bPlus);
        compatibleDonors.put("AB-", abMinus);
        compatibleDonors.put("AB+", abPlus);
        compatibleDonors.put("O-", oMinus);
        compatibleDonors.put("O+", oPlus);
    }

    public static ArrayList<String> getCompatibleDonorGroups(String recipientGroup) {
        ArrayList<String> result = compatibleDonors.get(recipientGroup.toUpperCase());
        if (result == null) return new ArrayList<>();
        return result;
    }

    public static boolean canDonate(String donorGroup, String recipientGroup) {
        ArrayList<String> compatible = getCompatibleDonorGroups(recipientGroup);
        return compatible.contains(donorGroup.toUpperCase());
    }
}
