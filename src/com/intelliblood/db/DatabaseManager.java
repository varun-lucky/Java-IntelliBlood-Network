package com.intelliblood.db;

import com.intelliblood.donor.Donor;
import com.intelliblood.donor.DonorRepository;

import java.sql.*;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:intelliblood.db";;

    private Connection conn;
    private boolean connected = false;

    public DatabaseManager() {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(DB_URL);

            createTables();
            connected = true;

            System.out.println("Database connected successfully!");

        } catch (Exception e) {
            connected = false;
            System.out.println("Database connection failed:");
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return connected;
    }

    // ---------- CREATE TABLES ----------
    private void createTables() throws SQLException {
        Statement st = conn.createStatement();

        st.execute("CREATE TABLE IF NOT EXISTS donors (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "blood_group TEXT NOT NULL," +
                "contact TEXT UNIQUE," +
                "city TEXT," +
                "age INTEGER," +
                "weight INTEGER," +
                "days_since_donation INTEGER," +
                "available INTEGER DEFAULT 1," +
                "verified INTEGER DEFAULT 0)");

        st.execute("CREATE TABLE IF NOT EXISTS requests (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "hospital TEXT," +
                "patient_name TEXT," +
                "blood_group TEXT," +
                "city TEXT," +
                "urgency TEXT," +
                "matched_count INTEGER DEFAULT 0," +
                "request_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

        st.close();
    }

    // ---------- SAVE DONOR ----------
    public void saveDonor(Donor d) {

        if (!connected) {
            System.out.println("Reconnecting DB...");
            try {
                conn = DriverManager.getConnection(DB_URL);
                connected = true;
            } catch (Exception e) {
                System.out.println("Still not connected!");
                return;
            }
        }

        try {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT OR IGNORE INTO donors VALUES (NULL,?,?,?,?,?,?,?,?,?)");

            ps.setString(1, d.getName());
            ps.setString(2, d.getBloodGroup());
            ps.setString(3, d.getContact());
            ps.setString(4, d.getCity());
            ps.setInt(5, d.getAge());
            ps.setInt(6, d.getWeight());
            ps.setInt(7, d.getDaysSinceLastDonation());
            ps.setInt(8, d.isAvailable() ? 1 : 0);
            ps.setInt(9, d.isVerified() ? 1 : 0);

            ps.executeUpdate();
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ---------- UPDATE VERIFICATION ----------
    public void updateDonorVerification(String contact, boolean verified) {
        if (!connected)
            return;

        try {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE donors SET verified=? WHERE contact=?");

            ps.setInt(1, verified ? 1 : 0);
            ps.setString(2, contact);

            ps.executeUpdate();
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ---------- SAVE REQUEST ----------
    public void saveRequest(String hospital, String patient, String bg,
            String city, String urgency, int matchedCount) {

        if (!connected) {
            System.out.println("DB not connected!");
            return;
        }

        try {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO requests (hospital, patient_name, blood_group, city, urgency, matched_count) " +
                            "VALUES (?,?,?,?,?,?)");

            ps.setString(1, hospital);
            ps.setString(2, patient);
            ps.setString(3, bg);
            ps.setString(4, city);
            ps.setString(5, urgency);
            ps.setInt(6, matchedCount);

            ps.executeUpdate();
            ps.close();

            System.out.println("Request saved successfully!");

        } catch (SQLException e) {
            System.out.println("Error saving request:");
            e.printStackTrace();
        }
    }

    // ---------- LOAD DONORS ----------
    public void loadDonorsIntoRepository(DonorRepository<Donor> repo) {
        if (!connected)
            return;

        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM donors");

            while (rs.next()) {
                Donor d = new Donor(
                        rs.getString("name"),
                        rs.getString("blood_group"),
                        rs.getString("contact"),
                        rs.getString("city"),
                        rs.getInt("age"),
                        rs.getInt("weight"),
                        rs.getInt("days_since_donation"));

                if (rs.getInt("available") == 0)
                    d.setAvailable(false);

                if (rs.getInt("verified") == 1)
                    d.setVerified(true);

                repo.addDonor(d);
            }

            rs.close();
            st.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ---------- GET REQUEST HISTORY ----------
    public ResultSet getAllRequests() {
        if (!connected)
            return null;

        try {
            Statement st = conn.createStatement();
            return st.executeQuery("SELECT * FROM requests ORDER BY request_time DESC");

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // ---------- CHECK EMPTY ----------
    public boolean isDatabaseEmpty() {
        if (!connected)
            return true;

        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM donors");

            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }

            rs.close();
            st.close();

            return count == 0;

        } catch (SQLException e) {
            return true;
        }
    }

    // ---------- CLOSE ----------
    public void close() {
        if (!connected)
            return;

        try {
            if (conn != null)
                conn.close();

        } catch (SQLException e) {
            System.out.println("Error closing DB: " + e.getMessage());
        }
    }
}