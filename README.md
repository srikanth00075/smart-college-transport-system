# 🚌 Smart College Transport Management System 

An enterprise-grade, comprehensive Java application for college bus fleet management, student seat allocations, driver telemetry, attendance tracking, grievance redressal, and automated broadcast notices.

## 🏛️ Project Architecture & Directory Layout

```text
SmartCollegeTransport/
│
├── database/
│   └── transport.sql              # Relational MySQL DDL & initial mock data
│
├── src/
│   ├── Main.java                  
│   │
│   ├── model/                     
│   │   ├── Loginable.java         
│   │   ├── Trackable.java        
│   │   ├── User.java              
│   │   ├── Student.java           
│   │   ├── Driver.java            
│   │   ├── Admin.java             
│   │   ├── Bus.java               
│   │   ├── Route.java             
│   │   ├── Complaint.java         
│   │   ├── AttendanceRecord.java  
│   │   └── Announcement.java      
│   │
│   ├── exception/                 
│   │   ├── BusFullException.java
│   │   ├── InvalidLoginException.java
│   │   └── InvalidRouteException.java
│   │
│   ├── database/                  
│   │   └── DBConnection.java      
│   │
│   ├── service/                   
│   │   ├── LoginService.java      
│   │   ├── BusService.java        
│   │   ├── RouteService.java      
│   │   ├── AttendanceService.java 
│   │   ├── ComplaintService.java 
│   │   ├── NotificationService.java 
│   │   └── DataPersistenceService.java 
│   │
│   ├── gui/                       #  Swing Desktop Interface
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
