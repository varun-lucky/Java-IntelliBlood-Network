package com.intelliblood.io;

import com.intelliblood.donor.Donor;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class FileExporter {

    private static final String EXPORT_FOLDER = "exports/";

    public FileExporter() {
        File folder = new File(EXPORT_FOLDER);
        if (!folder.exists()) folder.mkdirs();
    }

    public String exportDonorList(ArrayList<Donor> donors) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = EXPORT_FOLDER + "donors_" + timestamp + ".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("============================================");
            writer.newLine();
            writer.write("        INTELLIBLOOD NETWORK");
            writer.newLine();
            writer.write("        Donor Report - " + new Date());
            writer.newLine();
            writer.write("============================================");
            writer.newLine();
            writer.write("Total Donors: " + donors.size());
            writer.newLine();
            writer.newLine();
            int i = 1;
            for (Donor d : donors) {
                writer.write(i++ + ". " + d.toString());
                writer.newLine();
            }
            writer.newLine();
            writer.write("--- End of Report ---");
            writer.newLine();
            return filename;
        } catch (IOException e) {
            return null;
        }
    }

    public String exportMatchReport(String hospital, String patient, String bloodGroup,
                                    String city, String urgency, ArrayList<Donor> matches) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = EXPORT_FOLDER + "match_" + timestamp + ".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("============================================");
            writer.newLine();
            writer.write("     EMERGENCY MATCH REPORT");
            writer.newLine();
            writer.write("     " + new Date());
            writer.newLine();
            writer.write("============================================");
            writer.newLine();
            writer.write("Hospital    : " + hospital); writer.newLine();
            writer.write("Patient     : " + patient); writer.newLine();
            writer.write("Blood Group : " + bloodGroup); writer.newLine();
            writer.write("City        : " + city); writer.newLine();
            writer.write("Urgency     : " + urgency); writer.newLine();
            writer.newLine();
            writer.write("--- Matched Donors (" + matches.size() + ") ---");
            writer.newLine();
            int i = 1;
            for (Donor d : matches) {
                writer.write(i++ + ". " + d.toString());
                writer.newLine();
            }
            writer.newLine();
            writer.write("NOTE: Contact donors immediately. Same-city donors listed first.");
            writer.newLine();
            return filename;
        } catch (IOException e) {
            return null;
        }
    }

    public String exportBloodGroupStats(HashMap<String, Integer> stats) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = EXPORT_FOLDER + "stats_" + timestamp + ".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("============================================");
            writer.newLine();
            writer.write("     BLOOD GROUP STATISTICS");
            writer.newLine();
            writer.write("     " + new Date());
            writer.newLine();
            writer.write("============================================");
            writer.newLine();
            for (String bg : stats.keySet()) {
                writer.write(bg + "  :  " + stats.get(bg) + " donor(s)");
                writer.newLine();
            }
            return filename;
        } catch (IOException e) {
            return null;
        }
    }
}
