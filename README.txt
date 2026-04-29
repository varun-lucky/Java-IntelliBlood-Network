=====================================================
  INTELLIBLOOD NETWORK - FINAL VERSION
  Team LifeSaver | Chandigarh University
=====================================================

PACKAGE STRUCTURE
------------------
src/
└── com/intelliblood/
    ├── donor/
    │   ├── Donor.java                     (OOP, Comparable, Serializable)
    │   ├── DonorRepository.java           (Generics, ArrayList, HashMap, HashSet, LinkedList, Iterator)
    │   ├── BloodCompatibility.java        (WHO blood group compatibility matrix)
    │   ├── InvalidBloodGroupException.java (Custom checked exception)
    │   ├── NoDonorFoundException.java     (Custom checked exception)
    │   └── DuplicateDonorException.java   (Custom checked exception)
    ├── matching/
    │   ├── Matchable.java                 (Interface with abstract methods)
    │   ├── EligibilityChecker.java        (WHO eligibility rules: age, weight, days)
    │   └── MatchingEngine.java            (Implements Matchable, Multithreading, wait/notify)
    ├── request/
    │   └── EmergencyRequest.java          (OOP, validation, exception throwing)
    ├── db/
    │   └── DatabaseManager.java           (JDBC, Connection, Statement, ResultSet, PreparedStatement)
    ├── io/
    │   └── FileExporter.java              (BufferedWriter, FileWriter, Character Streams)
    ├── ui/
    │   └── MainFrame.java                 (Swing: JFrame, JTable, JTabbedPane, JComboBox,
    │                                        JTextArea, JTextField, JButton, JLabel, JCheckBox,
    │                                        JScrollPane, Event Handling, ActionListener, KeyAdapter,
    │                                        WindowAdapter, MouseAdapter, TableRowSorter)
    └── main/
        └── Main.java                      (Entry point, SwingUtilities)

SYLLABUS TOPICS USED
---------------------
Unit 1:
  - Variables, Data types, Constructors   → Donor.java
  - Arrays                                → EmergencyRequest.java (VALID_GROUPS[])
  - String handling, Scanner              → All input classes
  - Wrapper classes                       → Integer.parseInt() throughout

Unit 2:
  - OOP, Inheritance, Encapsulation       → Donor.java (private fields + getters)
  - Packages                              → All 7 packages
  - Interface                             → Matchable.java
  - Abstract methods                      → Matchable interface
  - Access protection                     → private/public throughout

Unit 3:
  - Custom Exceptions (checked)           → 3 custom exception classes
  - try-catch-finally                     → DatabaseManager, Main
  - throws clause                         → findMatches(), EmergencyRequest()
  - I/O Streams (Character)              → FileExporter (BufferedWriter, FileWriter)
  - Multithreading (Runnable, Thread)     → MatchingEngine.findMatchesInBackground()
  - wait() and notify()                   → requestQueue in MatchingEngine (Producer-Consumer)

Unit 4:
  - ArrayList                             → DonorRepository, MatchingEngine
  - HashMap                               → DonorRepository (bloodGroupMap), BloodCompatibility
  - LinkedList                            → DonorRepository (recentActivities), MatchingEngine (queue)
  - HashSet                               → DonorRepository (registeredContacts - duplicate check)
  - Iterator (Iterable<T>)               → DonorRepository implements Iterable
  - Generics <T>                          → DonorRepository<T extends Donor>
  - Comparable<T>                         → Donor implements Comparable<Donor>
  - Swing Controls                        → All in MainFrame.java

Unit 5:
  - Event Handling (ActionListener)       → All buttons in MainFrame
  - WindowAdapter                         → Exit confirmation dialog
  - MouseAdapter                          → Hover effects on buttons
  - KeyAdapter                            → Enter key triggers search
  - TableRowSorter                        → Live search filter on donor table
  - JDBC (Connection, Statement)          → DatabaseManager
  - PreparedStatement, ResultSet          → DatabaseManager

NEW FEATURES vs PREVIOUS VERSION
----------------------------------
1.  Blood Compatibility Matrix      - O- donor now matches ALL blood groups (real WHO rules)
2.  Eligibility: Age + Weight       - Added age (18-65) and weight (>=45kg) checks
3.  Duplicate Prevention            - HashSet blocks same contact number twice
4.  Verified Donor System           - Toggle verification status per donor
5.  Patient Name in Request         - Requests now track patient name
6.  Live Search + Filter            - Search donors by name/city/blood group in real time
7.  5-Tab GUI                       - Register, All Donors, Emergency, History, Statistics
8.  Request History Tab             - View all past emergency requests from database
9.  Statistics Tab                  - Donor count + eligible count per blood group
10. Activity Log                    - Recent registrations shown in real time
11. Remove Donor                    - Remove selected donor from table and DB
12. Window Close Confirmation       - Confirms before exit (WindowAdapter)
13. Key Press Search                - Press Enter in search box to trigger filter
14. Hover Button Effects            - Buttons darken on hover (MouseAdapter)
15. Matched Count Saved to DB       - Requests store how many donors were matched

HOW TO RUN
-----------
STEP 1: Download SQLite JDBC driver
  URL: https://github.com/xerial/sqlite-jdbc/releases
  File: sqlite-jdbc-3.xx.x.jar
  Place it INSIDE the intelliblood_final folder (same level as src/)

STEP 2: Open terminal in the src/ folder

STEP 3: Compile (Windows)
  javac -cp ".;../sqlite-jdbc-3.46.1.0.jar" com/intelliblood/donor/*.java com/intelliblood/matching/*.java com/intelliblood/request/*.java com/intelliblood/db/*.java com/intelliblood/io/*.java com/intelliblood/ui/*.java com/intelliblood/main/Main.java

  Compile (Mac/Linux)
  javac -cp ".:../sqlite-jdbc-3.46.1.0.jar" com/intelliblood/donor/*.java com/intelliblood/matching/*.java com/intelliblood/request/*.java com/intelliblood/db/*.java com/intelliblood/io/*.java com/intelliblood/ui/*.java com/intelliblood/main/Main.java

STEP 4: Run (Windows)
  java -cp ".;../sqlite-jdbc-3.46.1.0.jar" com.intelliblood.main.Main

  Run (Mac/Linux)
  java -cp ".:../sqlite-jdbc-3.46.1.0.jar" com.intelliblood.main.Main

NOTE: Replace sqlite-jdbc-3.46.1.0.jar with the exact filename you downloaded.
      The intelliblood.db file and exports/ folder are created automatically.

=====================================================
