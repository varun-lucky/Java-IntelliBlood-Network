package com.intelliblood.ui;

import com.intelliblood.db.DatabaseManager;
import com.intelliblood.donor.*;
import com.intelliblood.io.FileExporter;
import com.intelliblood.matching.EligibilityChecker;
import com.intelliblood.matching.MatchingEngine;
import com.intelliblood.request.EmergencyRequest;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

public class MainFrame extends JFrame {

    private DonorRepository<Donor> repository;
    private MatchingEngine engine;
    private DatabaseManager dbManager;
    private FileExporter exporter;

    private DefaultTableModel donorTableModel;
    private DefaultTableModel historyTableModel;
    private JTable donorTable;
    private JTextArea resultArea;
    private JLabel statusLabel;
    private JLabel totalDonorsLabel;

    static final Color RED = new Color(192, 0, 0);
    static final Color LIGHT_RED = new Color(255, 235, 235);
    static final Color WHITE = Color.WHITE;
    static final Color BG = new Color(248, 248, 248);

    public MainFrame(DonorRepository<Donor> repository, MatchingEngine engine,
            DatabaseManager dbManager, FileExporter exporter) {
        this.repository = repository;
        this.engine = engine;
        this.dbManager = dbManager;
        this.exporter = exporter;

        setTitle("IntelliBlood Network — Emergency Blood Donor Matching System");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1100, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(MainFrame.this,
                        "Are you sure you want to exit IntelliBlood Network?",
                        "Confirm Exit", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    dbManager.close();
                    System.exit(0);
                }
            }

        });

        setLayout(new BorderLayout(0, 0));
        add(buildHeader(), BorderLayout.NORTH);
        add(buildTabs(), BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        SwingUtilities.invokeLater(() -> {
            refreshDonorTable();
            updateTotalLabel();
        });
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(RED);
        header.setBorder(new EmptyBorder(12, 20, 12, 20));

        JPanel left = new JPanel(new GridLayout(2, 1, 0, 2));
        left.setBackground(RED);

        JLabel title = new JLabel("INTELLIBLOOD NETWORK");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(WHITE);

        JLabel sub = new JLabel("Emergency Blood Donor Matching System  |  Team LifeSaver  |  Graphic Era University");
        sub.setFont(new Font("Arial", Font.PLAIN, 12));
        sub.setForeground(new Color(255, 210, 210));

        left.add(title);
        left.add(sub);

        totalDonorsLabel = new JLabel("Total Donors: 0");
        totalDonorsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalDonorsLabel.setForeground(WHITE);
        totalDonorsLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        header.add(left, BorderLayout.WEST);
        header.add(totalDonorsLabel, BorderLayout.EAST);
        return header;
    }

    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.BOLD, 13));
        tabs.setBackground(BG);

        tabs.addTab("  Register Donor  ", buildRegisterTab());
        tabs.addTab("  All Donors  ", buildDonorListTab());
        tabs.addTab("  Emergency Request  ", buildEmergencyTab());
        tabs.addTab("  Request History  ", buildHistoryTab());
        tabs.addTab("  Statistics  ", buildStatsTab());

        return tabs;
    }

    // ──────────────────────────────────────────
    // TAB 1: REGISTER DONOR
    // ──────────────────────────────────────────
    private JPanel buildRegisterTab() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(WHITE);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(WHITE);
        form.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(RED, 2),
                "  New Donor Registration  ",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14), RED));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = createField();
        JTextField contactField = createField();
        JTextField cityField = createField();
        JTextField ageField = createField();
        JTextField weightField = createField();
        JTextField daysField = createField();
        JComboBox<String> bgBox = new JComboBox<>(EmergencyRequest.getValidGroups());
        bgBox.setFont(new Font("Arial", Font.PLAIN, 13));

        JCheckBox verifiedBox = new JCheckBox("Mark as Verified Donor");
        verifiedBox.setFont(new Font("Arial", Font.PLAIN, 13));
        verifiedBox.setBackground(WHITE);

        String[] labels = { "Full Name *", "Blood Group *", "Contact (10 digits) *",
                "City *", "Age (18 to 65) *", "Weight in kg (min 45) *", "Days Since Last Donation *" };
        JComponent[] fields = { nameField, bgBox, contactField, cityField, ageField, weightField, daysField };

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0.35;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Arial", Font.BOLD, 13));
            form.add(lbl, gbc);
            gbc.gridx = 1;
            gbc.weightx = 0.65;
            form.add(fields[i], gbc);
        }

        gbc.gridx = 0;
        gbc.gridy = labels.length;
        gbc.gridwidth = 2;
        form.add(verifiedBox, gbc);

        JButton registerBtn = makeButton("Register Donor");
        JButton clearBtn = makeButton("Clear Fields");
        clearBtn.setBackground(new Color(100, 100, 100));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnRow.setBackground(WHITE);
        btnRow.add(registerBtn);
        btnRow.add(clearBtn);

        gbc.gridx = 0;
        gbc.gridy = labels.length + 1;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(16, 12, 8, 12);
        form.add(btnRow, gbc);

        registerBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText().trim();
                String bg = (String) bgBox.getSelectedItem();
                String contact = contactField.getText().trim();
                String city = cityField.getText().trim();
                String ageStr = ageField.getText().trim();
                String wtStr = weightField.getText().trim();
                String daysStr = daysField.getText().trim();

                if (name.isEmpty() || contact.isEmpty() || city.isEmpty()
                        || ageStr.isEmpty() || wtStr.isEmpty() || daysStr.isEmpty()) {
                    showError("Please fill in all required fields.");
                    return;
                }
                if (!contact.matches("\\d{10}")) {
                    showError("Contact must be exactly 10 digits.");
                    return;
                }

                int age, weight, days;
                try {
                    age = Integer.parseInt(ageStr);
                    weight = Integer.parseInt(wtStr);
                    days = Integer.parseInt(daysStr);
                } catch (NumberFormatException ex) {
                    showError("Age, Weight and Days must be valid numbers.");
                    return;
                }

                if (age < 1 || age > 120) {
                    showError("Enter a valid age.");
                    return;
                }
                if (weight < 1) {
                    showError("Enter a valid weight.");
                    return;
                }
                if (days < 0) {
                    showError("Days cannot be negative.");
                    return;
                }

                Donor donor = new Donor(name, bg, contact, city, age, weight, days);
                donor.setVerified(verifiedBox.isSelected());

                boolean added = repository.addDonor(donor);
                if (!added) {
                    showError("A donor with this contact number is already registered.");
                    return;
                }

                dbManager.saveDonor(donor);
                refreshDonorTable();
                updateTotalLabel();

                String eligibility = EligibilityChecker.check(donor)
                        ? "This donor is ELIGIBLE to donate."
                        : "Note: " + EligibilityChecker.getFailReason(donor) + " (ineligible currently)";

                nameField.setText("");
                contactField.setText("");
                cityField.setText("");
                ageField.setText("");
                weightField.setText("");
                daysField.setText("");
                verifiedBox.setSelected(false);

                setStatus("Donor registered: " + name);
                JOptionPane.showMessageDialog(MainFrame.this,
                        "Donor '" + name + "' registered successfully!\n" + eligibility,
                        "Registration Successful", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        clearBtn.addActionListener(e -> {
            nameField.setText("");
            contactField.setText("");
            cityField.setText("");
            ageField.setText("");
            weightField.setText("");
            daysField.setText("");
            verifiedBox.setSelected(false);
        });

        GridBagConstraints o = new GridBagConstraints();
        o.insets = new Insets(30, 60, 30, 60);
        outer.add(form, o);
        return outer;
    }

    // ──────────────────────────────────────────
    // TAB 2: ALL DONORS
    // ──────────────────────────────────────────
    private JPanel buildDonorListTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(WHITE);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel topBar = new JPanel(new BorderLayout(8, 0));
        topBar.setBackground(WHITE);

        JLabel heading = new JLabel("All Registered Donors");
        heading.setFont(new Font("Arial", Font.BOLD, 17));
        heading.setForeground(RED);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchPanel.setBackground(WHITE);
        JLabel searchLbl = new JLabel("Search:");
        searchLbl.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField searchField = new JTextField(18);
        searchField.setFont(new Font("Arial", Font.PLAIN, 13));
        JComboBox<String> filterBox = new JComboBox<>(new String[] { "All", "Name", "City", "Blood Group" });
        filterBox.setFont(new Font("Arial", Font.PLAIN, 12));
        JButton searchBtn = makeButton("Search");
        searchPanel.add(searchLbl);
        searchPanel.add(searchField);
        searchPanel.add(filterBox);
        searchPanel.add(searchBtn);

        topBar.add(heading, BorderLayout.WEST);
        topBar.add(searchPanel, BorderLayout.EAST);
        panel.add(topBar, BorderLayout.NORTH);

        String[] cols = { "Name", "Blood Group", "City", "Contact", "Age", "Weight(kg)", "Days Since Donation",
                "Eligible", "Verified" };
        donorTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        donorTable = new JTable(donorTableModel);
        donorTable.setFont(new Font("Arial", Font.PLAIN, 13));
        donorTable.setRowHeight(26);
        donorTable.setSelectionBackground(LIGHT_RED);
        donorTable.setGridColor(new Color(220, 220, 220));
        donorTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        donorTable.getTableHeader().setBackground(RED);
        donorTable.getTableHeader().setForeground(WHITE);
        donorTable.setAutoCreateRowSorter(true);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(donorTableModel);
        donorTable.setRowSorter(sorter);

        searchBtn.addActionListener(e -> {
            String query = searchField.getText().trim().toLowerCase();
            String filter = (String) filterBox.getSelectedItem();
            if (query.isEmpty()) {
                sorter.setRowFilter(null);
                return;
            }
            int col = -1;
            if ("Name".equals(filter))
                col = 0;
            else if ("City".equals(filter))
                col = 2;
            else if ("Blood Group".equals(filter))
                col = 1;

            if (col == -1)
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));
            else
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + query, col));
        });

        searchField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchBtn.doClick();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(donorTable);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        btnPanel.setBackground(WHITE);

        JButton refreshBtn = makeButton("Refresh");
        JButton exportBtn = makeButton("Export to File");
        JButton verifyBtn = makeButton("Toggle Verify");
        verifyBtn.setBackground(new Color(0, 120, 0));
        JButton removeBtn = makeButton("Remove Donor");
        removeBtn.setBackground(new Color(150, 0, 0));

        refreshBtn.addActionListener(e -> refreshDonorTable());

        exportBtn.addActionListener(e -> {
            ArrayList<Donor> all = repository.getAllDonors();
            if (all.isEmpty()) {
                showError("No donors to export.");
                return;
            }
            String path = exporter.exportDonorList(all);
            if (path != null) {
                setStatus("Exported: " + path);
                JOptionPane.showMessageDialog(MainFrame.this, "Exported!\nFile: " + path,
                        "Export Done", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        verifyBtn.addActionListener(e -> {
            int row = donorTable.getSelectedRow();
            if (row < 0) {
                showError("Select a donor row first.");
                return;
            }
            int modelRow = donorTable.convertRowIndexToModel(row);
            String contact = (String) donorTableModel.getValueAt(modelRow, 3);
            for (Donor d : repository.getAllDonors()) {
                if (d.getContact().equals(contact)) {
                    d.setVerified(!d.isVerified());
                    dbManager.updateDonorVerification(contact, d.isVerified());
                    refreshDonorTable();
                    setStatus(d.getName() + " verification set to: " + d.isVerified());
                    break;
                }
            }
        });

        removeBtn.addActionListener(e -> {
            int row = donorTable.getSelectedRow();
            if (row < 0) {
                showError("Select a donor to remove.");
                return;
            }
            int modelRow = donorTable.convertRowIndexToModel(row);
            String name = (String) donorTableModel.getValueAt(modelRow, 0);
            String contact = (String) donorTableModel.getValueAt(modelRow, 3);
            int confirm = JOptionPane.showConfirmDialog(MainFrame.this,
                    "Remove donor: " + name + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                repository.removeDonor(contact);
                refreshDonorTable();
                updateTotalLabel();
                setStatus("Removed: " + name);
            }
        });

        btnPanel.add(refreshBtn);
        btnPanel.add(exportBtn);
        btnPanel.add(verifyBtn);
        btnPanel.add(removeBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    // ──────────────────────────────────────────
    // TAB 3: EMERGENCY REQUEST
    // ──────────────────────────────────────────
    private JPanel buildEmergencyTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(WHITE);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(WHITE);
        form.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(RED, 2), "  Emergency Blood Request  ",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14), RED));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 12, 7, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField hospitalField = createField();
        JTextField patientField = createField();
        JTextField cityField = createField();
        JComboBox<String> bgBox = new JComboBox<>(EmergencyRequest.getValidGroups());
        bgBox.setFont(new Font("Arial", Font.PLAIN, 13));
        JComboBox<String> urgencyBox = new JComboBox<>(new String[] { "High", "Medium", "Low" });
        urgencyBox.setFont(new Font("Arial", Font.PLAIN, 13));

        JCheckBox compatibleCheck = new JCheckBox("Include compatible blood groups (e.g. O- for all)");
        compatibleCheck.setFont(new Font("Arial", Font.PLAIN, 12));
        compatibleCheck.setBackground(WHITE);
        compatibleCheck.setSelected(true);

        String[] labels = { "Hospital Name *", "Patient Name *", "Required Blood Group *",
                "City *", "Urgency Level *" };
        JComponent[] fields = { hospitalField, patientField, bgBox, cityField, urgencyBox };

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0.3;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Arial", Font.BOLD, 13));
            form.add(lbl, gbc);
            gbc.gridx = 1;
            gbc.weightx = 0.7;
            form.add(fields[i], gbc);
        }

        gbc.gridx = 0;
        gbc.gridy = labels.length;
        gbc.gridwidth = 2;
        form.add(compatibleCheck, gbc);

        JButton searchBtn = makeButton("  Find Matching Donors  ");
        gbc.gridy = labels.length + 1;
        gbc.insets = new Insets(14, 12, 8, 12);
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnRow.setBackground(WHITE);
        btnRow.add(searchBtn);
        form.add(btnRow, gbc);

        resultArea = new JTextArea(13, 50);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        resultArea.setEditable(false);
        resultArea.setBackground(new Color(245, 245, 245));
        resultArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        JScrollPane resultScroll = new JScrollPane(resultArea);
        resultScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(RED, 1), "  Match Results  ",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 13), RED));

        JPanel bottomBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        bottomBtns.setBackground(WHITE);
        JButton exportBtn = makeButton("Export Match Report");
        JButton clearBtn = makeButton("Clear");
        clearBtn.setBackground(new Color(100, 100, 100));
        bottomBtns.add(exportBtn);
        bottomBtns.add(clearBtn);
        clearBtn.addActionListener(e -> resultArea.setText(""));

        searchBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String hospital = hospitalField.getText().trim();
                String patient = patientField.getText().trim();
                String bg = (String) bgBox.getSelectedItem();
                String city = cityField.getText().trim();
                String urgency = (String) urgencyBox.getSelectedItem();

                if (hospital.isEmpty() || patient.isEmpty() || city.isEmpty()) {
                    showError("Please fill all required fields.");
                    return;
                }

                try {
                    EmergencyRequest req = new EmergencyRequest(hospital, bg, city, urgency, patient);
                    resultArea.setText(
                            "Searching for " + bg + " compatible donors near " + city + "...\n\nPlease wait...");
                    searchBtn.setEnabled(false);
                    setStatus("Searching for donors...");

                    engine.findMatchesInBackground(bg, city, new MatchingEngine.MatchCallback() {
                        public void onSuccess(ArrayList<Donor> donors) {
                            SwingUtilities.invokeLater(() -> {
                                dbManager.saveRequest(hospital, patient, bg, city, urgency, donors.size());

                                StringBuilder sb = new StringBuilder();
                                sb.append("REQUEST DETAILS\n");
                                sb.append("=".repeat(60)).append("\n");
                                sb.append(req.toString()).append("\n");
                                sb.append("=".repeat(60)).append("\n\n");
                                sb.append("COMPATIBLE DONORS FOUND: ").append(donors.size()).append("\n");
                                sb.append("(Includes all blood groups compatible with ").append(bg).append(")\n");
                                sb.append("-".repeat(60)).append("\n\n");

                                int i = 1;
                                for (Donor d : donors) {
                                    String loc = d.getCity().equalsIgnoreCase(city) ? " [SAME CITY]"
                                            : " [Other city: " + d.getCity() + "]";
                                    String ver = d.isVerified() ? " [Verified]" : " [Unverified]";
                                    sb.append(i++).append(". ").append(d.getName())
                                            .append(" | ").append(d.getBloodGroup())
                                            .append(" | Contact: ").append(d.getContact())
                                            .append(loc).append(ver).append("\n");
                                }

                                sb.append("\n--- Contact donors immediately in order shown ---");
                                resultArea.setText(sb.toString());
                                searchBtn.setEnabled(true);
                                setStatus("Match complete: " + donors.size() + " donor(s) found for " + bg + " in "
                                        + city);
                            });
                        }

                        public void onError(String message) {
                            SwingUtilities.invokeLater(() -> {
                                dbManager.saveRequest(hospital, patient, bg, city, urgency, 0);
                                resultArea.setText("NO DONORS FOUND\n\n" + message +
                                        "\n\nSuggestion: Try contacting the nearest blood bank directly.");
                                searchBtn.setEnabled(true);
                                setStatus("No donors found for " + bg + " in " + city);
                            });
                        }
                    });

                } catch (InvalidBloodGroupException ex) {
                    showError(ex.getMessage());
                }
            }
        });

        exportBtn.addActionListener(e -> {
            ArrayList<Donor> last = engine.getLastResult();
            if (last.isEmpty()) {
                showError("Run a search first.");
                return;
            }
            String hospital = hospitalField.getText().trim();
            String patient = patientField.getText().trim();
            String bg = (String) bgBox.getSelectedItem();
            String city = cityField.getText().trim();
            String urgency = (String) urgencyBox.getSelectedItem();
            String path = exporter.exportMatchReport(hospital, patient, bg, city, urgency, last);
            if (path != null) {
                setStatus("Report saved: " + path);
                JOptionPane.showMessageDialog(MainFrame.this,
                        "Match report saved!\nFile: " + path, "Exported", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        panel.add(form, BorderLayout.NORTH);
        panel.add(resultScroll, BorderLayout.CENTER);
        panel.add(bottomBtns, BorderLayout.SOUTH);
        return panel;
    }

    // ──────────────────────────────────────────
    // TAB 4: REQUEST HISTORY
    // ──────────────────────────────────────────
    private JPanel buildHistoryTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(WHITE);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel heading = new JLabel("Emergency Request History");
        heading.setFont(new Font("Arial", Font.BOLD, 17));
        heading.setForeground(RED);
        panel.add(heading, BorderLayout.NORTH);

        String[] cols = { "ID", "Hospital", "Patient", "Blood Group", "City", "Urgency", "Matches Found",
                "Date & Time" };
        historyTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable historyTable = new JTable(historyTableModel);
        historyTable.setFont(new Font("Arial", Font.PLAIN, 13));
        historyTable.setRowHeight(26);
        historyTable.setSelectionBackground(LIGHT_RED);
        historyTable.setGridColor(new Color(220, 220, 220));
        historyTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        historyTable.getTableHeader().setBackground(RED);
        historyTable.getTableHeader().setForeground(WHITE);
        historyTable.setAutoCreateRowSorter(true);

        JScrollPane scroll = new JScrollPane(historyTable);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        btnPanel.setBackground(WHITE);
        JButton loadBtn = makeButton("Load History");
        btnPanel.add(loadBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        loadBtn.addActionListener(e -> {
            historyTableModel.setRowCount(0);
            if (!dbManager.isConnected()) {
                showError("Database not connected. History not available.");
                return;
            }
            try {
                ResultSet rs = dbManager.getAllRequests();
                if (rs == null) {
                    showError("Could not load history.");
                    return;
                }
                int count = 0;
                while (rs.next()) {
                    historyTableModel.addRow(new Object[] {
                            rs.getInt("id"),
                            rs.getString("hospital"),
                            rs.getString("patient_name"),
                            rs.getString("blood_group"),
                            rs.getString("city"),
                            rs.getString("urgency"),
                            rs.getInt("matched_count"),
                            rs.getString("request_time")
                    });
                    count++;
                }
                setStatus("Loaded " + count + " request(s) from history.");
            } catch (Exception ex) {
                showError("Error loading history: " + ex.getMessage());
            }
        });

        return panel;
    }

    // ──────────────────────────────────────────
    // TAB 5: STATISTICS
    // ──────────────────────────────────────────
    private JPanel buildStatsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel heading = new JLabel("Blood Group Statistics & Activity Log");
        heading.setFont(new Font("Arial", Font.BOLD, 17));
        heading.setForeground(RED);
        panel.add(heading, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        centerPanel.setBackground(WHITE);

        JPanel statsPanel = new JPanel(new BorderLayout(0, 8));
        statsPanel.setBackground(WHITE);
        statsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(RED, 1), "  Donor Count by Blood Group  ",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 13), RED));

        String[] statCols = { "Blood Group", "Donor Count", "Eligible Count" };
        DefaultTableModel statsModel = new DefaultTableModel(statCols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable statsTable = new JTable(statsModel);
        statsTable.setFont(new Font("Arial", Font.PLAIN, 14));
        statsTable.setRowHeight(30);
        statsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        statsTable.getTableHeader().setBackground(RED);
        statsTable.getTableHeader().setForeground(WHITE);
        statsPanel.add(new JScrollPane(statsTable), BorderLayout.CENTER);

        JPanel activityPanel = new JPanel(new BorderLayout(0, 8));
        activityPanel.setBackground(WHITE);
        activityPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(RED, 1), "  Recent Activity Log  ",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 13), RED));

        JTextArea activityArea = new JTextArea();
        activityArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        activityArea.setEditable(false);
        activityArea.setBackground(new Color(245, 245, 245));
        activityPanel.add(new JScrollPane(activityArea), BorderLayout.CENTER);

        centerPanel.add(statsPanel);
        centerPanel.add(activityPanel);
        panel.add(centerPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        btnPanel.setBackground(WHITE);
        JButton refreshBtn = makeButton("Refresh Stats");
        JButton exportBtn = makeButton("Export Stats");
        btnPanel.add(refreshBtn);
        btnPanel.add(exportBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> {
            statsModel.setRowCount(0);
            HashMap<String, Integer> stats = repository.getBloodGroupStats();
            for (String bg : stats.keySet()) {
                int total = stats.get(bg);
                int eligible = 0;
                for (Donor d : repository.getByBloodGroup(bg)) {
                    if (EligibilityChecker.check(d))
                        eligible++;
                }
                statsModel.addRow(new Object[] { bg, total, eligible });
            }

            StringBuilder activity = new StringBuilder();
            for (String log : repository.getRecentActivities()) {
                activity.append("• ").append(log).append("\n");
            }
            activityArea.setText(activity.toString());
            setStatus("Statistics refreshed.");
        });

        exportBtn.addActionListener(e -> {
            HashMap<String, Integer> stats = repository.getBloodGroupStats();
            if (stats.isEmpty()) {
                showError("No data to export.");
                return;
            }
            String path = exporter.exportBloodGroupStats(stats);
            if (path != null) {
                setStatus("Stats exported: " + path);
                JOptionPane.showMessageDialog(MainFrame.this,
                        "Stats exported!\nFile: " + path, "Exported", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        return panel;
    }

    // ──────────────────────────────────────────
    // HELPERS
    // ──────────────────────────────────────────
    private void refreshDonorTable() {
        if (donorTableModel == null)
            return;

        donorTableModel.setRowCount(0);

        System.out.println("UI loading donors: " + repository.getAllDonors().size()); // debug

        for (Donor d : repository.getAllDonors()) {
            donorTableModel.addRow(new Object[] {
                    d.getName(),
                    d.getBloodGroup(),
                    d.getCity(),
                    d.getContact(),
                    d.getAge(),
                    d.getWeight(),
                    d.getDaysSinceLastDonation(),
                    EligibilityChecker.check(d) ? "Yes" : "No",
                    d.isVerified() ? "Yes" : "No"
            });
        }
    }

    private void updateTotalLabel() {
        totalDonorsLabel.setText("Total Donors: " + repository.getTotalCount() + "  ");
    }

    private JTextField createField() {
        JTextField f = new JTextField(20);
        f.setFont(new Font("Arial", Font.PLAIN, 13));
        return f;
    }

    private JButton makeButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(RED);
        btn.setForeground(WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.addMouseListener(new MouseAdapter() {
            Color orig = btn.getBackground();

            public void mouseEntered(MouseEvent e) {
                btn.setBackground(btn.getBackground().darker());
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(orig);
            }
        });
        return btn;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void setStatus(String msg) {
        statusLabel.setText("  " + msg);
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(225, 225, 225));
        bar.setBorder(new EmptyBorder(4, 8, 4, 8));
        statusLabel = new JLabel("  Ready");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        JLabel copy = new JLabel("IntelliBlood Network  |  Team LifeSaver  ");
        copy.setFont(new Font("Arial", Font.PLAIN, 11));
        copy.setForeground(Color.GRAY);
        bar.add(statusLabel, BorderLayout.WEST);
        bar.add(copy, BorderLayout.EAST);
        return bar;
    }
}
