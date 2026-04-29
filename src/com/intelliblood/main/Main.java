package com.intelliblood.main;

import com.intelliblood.db.DatabaseManager;
import com.intelliblood.donor.Donor;
import com.intelliblood.donor.DonorRepository;
import com.intelliblood.io.FileExporter;
import com.intelliblood.matching.MatchingEngine;
import com.intelliblood.ui.MainFrame;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
            }

            DatabaseManager db = new DatabaseManager();
            DonorRepository<Donor> repository = new DonorRepository<>();

            // 🔥 Try loading from DB
            db.loadDonorsIntoRepository(repository);

            // 🔥 IF EMPTY → FORCE ADD SAMPLE (THIS IS THE KEY FIX)
            if (repository.getAllDonors().isEmpty()) {
                System.out.println("DB empty → Loading sample donors into UI");

                loadSampleData(repository, db);
            }

            // 🔥 DEBUG
            System.out.println("Donors in repo: " + repository.getAllDonors().size());

            MatchingEngine engine = new MatchingEngine(repository);
            FileExporter exporter = new FileExporter();

            MainFrame frame = new MainFrame(repository, engine, db, exporter);
            frame.setVisible(true);
        });
    }

    // ---------- SAMPLE DATA ----------
    static void loadSampleData(DonorRepository<Donor> repo, DatabaseManager db) {

        Donor[] sample = {
                new Donor("Suryansh Yadav", "B+", "9876543210", "Delhi", 22, 70, 120),
                new Donor("Riya Saini", "O+", "9812345678", "Delhi", 21, 55, 200),
                new Donor("Varun Singh", "A+", "9988776655", "Mumbai", 23, 68, 95),
                new Donor("Rishabh Yadav", "B+", "9001122334", "Delhi", 20, 60, 60),
                new Donor("Anjali Mehta", "AB-", "9123456789", "Jaipur", 25, 52, 150),
                new Donor("Rohit Sharma", "O-", "9234567890", "Delhi", 30, 75, 110),
                new Donor("Priya Verma", "A-", "9345678901", "Mumbai", 27, 50, 180),
                new Donor("Amit Kumar", "B-", "9456789012", "Pune", 35, 80, 95),
                new Donor("Neha Gupta", "O+", "9567890123", "Mumbai", 24, 58, 130),
                new Donor("Rahul Singh", "AB+", "9678901234", "Delhi", 28, 72, 200),
                new Donor("Pooja Sharma", "O-", "9789012345", "Chennai", 32, 54, 170),
                new Donor("Vikram Patel", "A+", "9890123456", "Pune", 40, 85, 140),
                new Donor("Sneha Iyer", "B+", "9901234567", "Mumbai", 26, 62, 100),
                new Donor("Aditya Mishra", "AB+", "9012345678", "Delhi", 19, 65, 95),
                new Donor("Kavya Reddy", "O+", "8901234567", "Hyderabad", 23, 57, 210)
        };

        for (Donor d : sample) {
            if (d.getDaysSinceLastDonation() >= 90)
                d.setVerified(true);

            repo.addDonor(d);
            db.saveDonor(d);
        }

        System.out.println("Sample data inserted into DB.");
    }
}