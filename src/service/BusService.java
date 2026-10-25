package service;

import database.DBConnection;
import exception.BusFullException;
import model.Bus;
import model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CS23304 Units Covered:
 * - Unit 2: Method Overloading (multiple searchBus methods with distinct signatures)
 * - Unit 3: User-defined Exception handling (BusFullException)
 * - Unit 4: Collections Framework (ArrayList, HashMap, HashSet), Lambda Expressions & Streams API
 * - Unit 5: JDBC integration
 */
public class BusService {
    private Map<String, Bus> busMap;

    public BusService() {
        this.busMap = new LinkedHashMap<>();
        seedDefaultBuses();
    }

    private void seedDefaultBuses() {
        // Bus 101 - Route 1, plenty of space
        Bus bus101 = new Bus("BUS-101", "KA-01-EXP-101", 50, 1, "USR-DRV-1");
        bus101.updateLocation("City Center Mall");
        bus101.setStatus("On Time");
        try {
            bus101.registerStudent("USR-STU-1");
            bus101.registerStudent("USR-STU-2");
        } catch (BusFullException ignored) {}

        // Bus 102 - Route 2, FULL BUS TO DEMONSTRATE BusFullException!
        // Exactly matches the user's workflow test case:
        // "Bus has 50 seats -> 50 seats occupied -> Student tries to register -> BusFullException -> Sorry! Bus is full."
        Bus bus102 = new Bus("BUS-102", "KA-01-EXP-102", 50, 2, "USR-DRV-2");
        bus102.updateLocation("Metro Station Gate 2");
        bus102.setStatus("Delayed 10 min");
        for (int i = 1; i <= 50; i++) {
            try {
                bus102.registerStudent("DUMMY-STU-" + i);
            } catch (BusFullException ignored) {}
        }

        // Bus 103 - Route 3
        Bus bus103 = new Bus("BUS-103", "KA-01-EXP-103", 45, 3, null);
        bus103.updateLocation("Campus Bus Depot");
        bus103.setStatus("Idle");

        // Bus 104 - Route 1 backup
        Bus bus104 = new Bus("BUS-104", "KA-01-EXP-104", 40, 1, null);
        bus104.updateLocation("Green Park Colony");
        bus104.setStatus("On Time");

        busMap.put(bus101.getBusId(), bus101);
        busMap.put(bus102.getBusId(), bus102);
        busMap.put(bus103.getBusId(), bus103);
        busMap.put(bus104.getBusId(), bus104);
    }

    // ==========================================================
    // CS23304 - Unit 2: Method Overloading Demonstration
    // Three overloaded variants of searchBus with distinct parameter lists
    // ==========================================================

    /**
     * Overload #1: Search bus by Bus Number or Bus ID (String parameter).
     */
    public Optional<Bus> searchBus(String query) {
        if (query == null) return Optional.empty();
        String q = query.trim().toUpperCase();
        return busMap.values().stream()
                .filter(b -> b.getBusId().equalsIgnoreCase(q) || b.getBusNumber().toUpperCase().contains(q))
                .findFirst();
    }

    /**
     * Overload #2: Search all buses assigned to a specific Route ID (int parameter).
     * Unit 4: Demonstrates Java Stream & Lambda Expression.
     */
    public List<Bus> searchBus(int routeId) {
        return busMap.values().stream()
                .filter(b -> b.getRouteId() == routeId)
                .collect(Collectors.toList());
    }

    /**
     * Overload #3: Search buses with seat availability criteria (int, boolean parameters).
     */
    public List<Bus> searchBus(int minAvailableSeats, boolean onlyActiveRoutes) {
        return busMap.values().stream()
                .filter(b -> b.getAvailableSeats() >= minAvailableSeats)
                .filter(b -> !onlyActiveRoutes || (b.getRouteId() > 0 && !"Maintenance".equalsIgnoreCase(b.getStatus())))
                .sorted(Comparator.comparingInt(Bus::getAvailableSeats).reversed())
                .collect(Collectors.toList());
    }

    // ==========================================================
    // Unit 3: Exception Handling Workflow
    // ==========================================================

    /**
     * Attempts to register a student for a bus.
     * Throws BusFullException if seat capacity is exhausted.
     */
    public synchronized void registerStudentToBus(String busId, Student student) throws BusFullException {
        Bus bus = busMap.get(busId);
        if (bus == null) {
            throw new IllegalArgumentException("Bus with ID '" + busId + "' not found.");
        }

        // If student was previously assigned to another bus, unregister them first
        if (student.getAssignedBusId() != null && !student.getAssignedBusId().equals(busId)) {
            Bus oldBus = busMap.get(student.getAssignedBusId());
            if (oldBus != null) {
                oldBus.unregisterStudent(student.getId());
            }
        }

        // Unit 3: Throws BusFullException if bus is full
        bus.registerStudent(student.getId());

        // Update student model
        student.setAssignedBusId(bus.getBusId());

        // Unit 5: Update MySQL database if available
        if (DBConnection.isDatabaseAvailable()) {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                try {
                    String sql = "UPDATE students SET assigned_bus_id = ? WHERE student_id = ?";
                    try (PreparedStatement pst = conn.prepareStatement(sql)) {
                        pst.setString(1, bus.getBusId());
                        pst.setString(2, student.getId());
                        pst.executeUpdate();
                    }
                } catch (SQLException e) {
                    System.err.println("[JDBC Bus Assignment Error] " + e.getMessage());
                }
            }
        }
    }

    public synchronized void addBus(Bus bus) {
        if (bus != null) {
            busMap.put(bus.getBusId(), bus);
        }
    }

    public synchronized boolean removeBus(String busId) {
        return busMap.remove(busId) != null;
    }

    public List<Bus> getAllBuses() {
        return new ArrayList<>(busMap.values());
    }

    public Bus getBusById(String busId) {
        return busMap.get(busId);
    }
}
