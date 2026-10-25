# 🚌 Smart College Transport Management System (CS23304)

An enterprise-grade, comprehensive Java application for college bus fleet management, student seat allocations, driver telemetry, attendance tracking, grievance redressal, and automated broadcast notices.

This project is tailored specifically for the **CS23304: Advanced Java Programming** curriculum, demonstrating clean design patterns and ensuring **all 5 units of Java** appear naturally.

---

## 📑 CS23304 Syllabus Mapping (Units 1 - 5)

| Unit | Syllabus Topic | Where & How It Is Used in This Project | Source File(s) |
|---|---|---|---|
| **Unit 1** | **Classes & Objects** | Real-world entity modeling (`Student`, `Driver`, `Admin`, `Bus`, `Route`, `Complaint`, `AttendanceRecord`) | `model/*.java` |
| **Unit 1** | **Constructors & `this`** | Default, overloaded, and parameterized constructors using `this` and `super` chaining | `model/User.java`, `model/Student.java`, `model/Bus.java` |
| **Unit 1** | **`static` Members** | Auto-incrementing synchronized ID generators (`generateNextId()`) and system constants | `model/User.java`, `model/Complaint.java` |
| **Unit 1** | **String & Date/Time API** | Formatted timetables, bus schedules, and timestamps using `LocalDate`, `LocalTime`, `LocalDateTime`, `DateTimeFormatter` | `model/Route.java`, `model/AttendanceRecord.java`, `model/Announcement.java` |
| **Unit 2** | **Inheritance** | Hierarchical inheritance: abstract `User` superclass extended by `Student`, `Driver`, and `Admin` | `model/User.java`, `model/Student.java`, `model/Driver.java`, `model/Admin.java` |
| **Unit 2** | **Abstract Classes** | Abstract base class `User` with abstract polymorphic contract `displayDashboardInfo()` | `model/User.java` |
| **Unit 2** | **Interfaces** | Multiple interface contracts: `Loginable` (authentication) and `Trackable` (live location updates) | `model/Loginable.java`, `model/Trackable.java` |
| **Unit 2** | **Method Overloading** | Overloaded search variations in services: `searchBus(String)`, `searchBus(int routeId)`, `searchBus(int seats, boolean active)`, `searchRoute(int)`, `searchRoute(String)` | `service/BusService.java`, `service/RouteService.java` |
| **Unit 2** | **Method Overriding** | Subclasses override `getRole()`, `displayDashboardInfo()`, `toString()` | `model/Student.java`, `model/Driver.java` |
| **Unit 2** | **Packages** | Clean multi-package architecture: `model`, `service`, `exception`, `database`, `gui`, `test` | `src/*` |
| **Unit 3** | **Exception Handling** | `try`, `catch`, `finally`, `throw`, `throws` for validation & error bounds | `service/LoginService.java`, `gui/LoginFrame.java` |
| **Unit 3** | **User-Defined Exceptions** | Custom checked exceptions: `BusFullException`, `InvalidLoginException`, `InvalidRouteException` | `exception/*.java` |
| **Unit 3** | **Multithreading** | Background Daemon Thread (`NotificationService extends Thread`) simulating live bus GPS tracking and broadcasting notices | `service/NotificationService.java` |
| **Unit 4** | **Collections Framework** | `ArrayList` for dynamic records, `LinkedHashMap` for fast key-value lookups, `HashSet` to eliminate duplicate seat registrations | `model/Bus.java`, `service/LoginService.java`, `service/BusService.java` |
| **Unit 4** | **Lambda & Streams** | Stream filtering, predicate matching, sorting, and attendance percentage aggregations | `service/BusService.java`, `service/AttendanceService.java` |
| **Unit 4** | **File Handling** | CSV attendance export via `BufferedWriter`/`FileWriter`, formatted text grievance logs via `PrintWriter`, and audit logging | `service/DataPersistenceService.java` |
| **Unit 4** | **Object Serialization** | Full fleet binary snapshot persistence using `ObjectOutputStream` & `ObjectInputStream` (`Serializable`) | `service/DataPersistenceService.java`, `gui/AdminDashboard.java` |
| **Unit 5** | **JDBC & MySQL** | Relational schema `transport.sql`, `DBConnection.java` with prepared statements (`PreparedStatement`, `ResultSet`, `DriverManager`) and automatic resilient local fallback | `database/DBConnection.java`, `database/transport.sql` |
| **Unit 5** | **AWT / Swing GUI** | Modern graphical user interfaces: `LoginFrame`, `StudentDashboard`, `AdminDashboard`, `DriverDashboard` | `gui/*.java` |
| **Unit 5** | **Event Handling** | Action listeners, mouse hover events, table selection models, modal dialogs | `gui/*.java` |

---

## 🏛️ Project Architecture & Directory Layout

```text
SmartCollegeTransport/
│
├── database/
│   └── transport.sql              # Relational MySQL DDL & initial mock data
│
├── src/
│   ├── Main.java                  # Application Bootstrap & Swing GUI entry
│   │
│   ├── model/                     # Unit 1 & Unit 2: Object-Oriented Domain Models
│   │   ├── Loginable.java         # Interface for authentication contract
│   │   ├── Trackable.java         # Interface for geolocation updates
│   │   ├── User.java              # Abstract Superclass (Implements Loginable, Serializable)
│   │   ├── Student.java           # Subclass (Extends User, implements Trackable)
│   │   ├── Driver.java            # Subclass (Extends User)
│   │   ├── Admin.java             # Subclass (Extends User)
│   │   ├── Bus.java               # Bus entity with seat tracking & capacity enforcement
│   │   ├── Route.java             # Route entity with timetable & ordered stops
│   │   ├── Complaint.java         # Student grievance model with resolution workflow
│   │   ├── AttendanceRecord.java  # Boarding record with Date/Time API
│   │   └── Announcement.java      # Broadcast notice entity
│   │
│   ├── exception/                 # Unit 3: User-defined Custom Exceptions
│   │   ├── BusFullException.java
│   │   ├── InvalidLoginException.java
│   │   └── InvalidRouteException.java
│   │
│   ├── database/                  # Unit 5: JDBC Integration
│   │   └── DBConnection.java      # MySQL Connection Manager with resilient fallback
│   │
│   ├── service/                   # Core Business Logic & Algorithms
│   │   ├── LoginService.java      # Credential verification & registration
│   │   ├── BusService.java        # Fleet management & overloaded search algorithms
│   │   ├── RouteService.java      # Route traversal & stop validation
│   │   ├── AttendanceService.java # Boarding logs & percentage calculations
│   │   ├── ComplaintService.java  # Student grievances & admin remark updates
│   │   ├── NotificationService.java # Unit 3: Multithreaded background daemon
│   │   └── DataPersistenceService.java # Unit 4: File I/O & Object Serialization
│   │
│   ├── gui/                       # Unit 5: Swing Desktop Interface
│   │   ├── UIUtils.java           # Color palette, buttons, card borders & styles
│   │   ├── LoginFrame.java        # Gateway with role toggles & 1-click viva demo fillers
│   │   ├── StudentDashboard.java  # Student portal with registration workflow
│   │   ├── AdminDashboard.java    # Administrator control panel
│   │   └── DriverDashboard.java   # Driver operations & delay broadcasting console
│   │
│   └── test/
│       └── WorkflowTest.java      # Automated headless verification suite
│
├── bin/                           # Compiled bytecode (.class files)
└── README.md
```

---

## ⚡ Key Workflows Demonstrated

### 1. Student Bus Registration Workflow
```text
Login
  ↓
Student Dashboard
  ↓
Select "Available Routes & Registration" Tab
  ↓
Select Route (e.g., Route 1 - South Central Line)
  ↓
Select Bus (e.g., KA-01-EXP-101)
  ↓
Check Seat Availability (48 free seats remaining)
  ↓
Click "Confirm Seat & Enroll to Bus"
  ↓
Database & Student Profile Updated!
```

### 2. Bus Full Scenario (`BusFullException`)
```text
Bus KA-01-EXP-102 (Route 2) has 50 seats
        ↓
50 seats occupied (Preloaded demo full bus)
        ↓
Student attempts to enroll
        ↓
throws new BusFullException("KA-01-EXP-102", 50)
        ↓
Modal Alert: "Sorry! Bus KA-01-EXP-102 is full (All 50 seats are occupied)."
```

### 3. Driver Delay Broadcast & Real-time Update (Multithreading)
```text
Driver logs into console
        ↓
Selects "Report Delay" tab -> selects "Delayed by 15 mins (Heavy Traffic)"
        ↓
Clicks "Broadcast Live Status Update"
        ↓
NotificationService Daemon broadcasts announcement to connected students
        ↓
Student Dashboard updates live operating status in real-time!
```

---

## 🚀 How to Compile and Run

### Option 1: Command Line (PowerShell / Terminal)

1. Open PowerShell and navigate to the project directory:
   ```powershell
   cd C:\Users\HP\.gemini\antigravity\scratch\SmartCollegeTransport
   ```

2. Compile all source files into the `bin` directory:
   ```powershell
   javac -d bin (Get-ChildItem -Recurse -Path src -Filter *.java | ForEach-Object { $_.FullName })
   ```

3. Run the Automated Syllabus Test Suite:
   ```powershell
   java -cp bin test.WorkflowTest
   ```

4. Launch the Graphical Desktop Application:
   ```powershell
   java -cp bin Main
   ```

---

## 🔑 Preconfigured Demo Accounts (For Viva & Presentation)

For effortless presentation during project defense, the `LoginFrame` includes **Quick Fill Demo Buttons**:

| Role | Email Address | Password | Key Capabilities |
|---|---|---|---|
| **Student** | `aarav.student@college.edu` | `student123` | View assigned bus, register for routes, mark morning/evening attendance, submit complaints, view notices |
| **Student (Unassigned)** | `rohan.student@college.edu` | `student123` | Test the interactive seat enrollment workflow from scratch |
| **Transport Admin** | `admin@college.edu` | `admin123` | Add/delete buses, create routes, link drivers, resolve complaints, broadcast alerts, export CSV/TXT, serialize backup |
| **Driver** | `ramesh.driver@college.edu` | `driver123` | View route stops & timings, mark student boarding checklist, report traffic delay / breakdowns |

---

## 🗄️ Database Setup (Optional)

The application features a **Dual-Mode Persistence Architecture**:
- **Offline / Standalone Mode**: Works immediately out of the box using high-performance In-Memory state and binary file serialization without requiring any MySQL installation.
- **MySQL Mode**: If a MySQL server is running on `localhost:3306`, execute the provided script:
  ```sql
  mysql -u root -p < database/transport.sql
  ```
  The application automatically detects the database connection and commits live updates to MySQL tables.
