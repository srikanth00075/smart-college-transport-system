package model;

import exception.BusFullException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * CS23304 Units Covered:
 * - Unit 1: Classes & Objects, Constructors, 'this' keyword
 * - Unit 2: Interfaces (Trackable), Method Overriding
 * - Unit 3: Exception Handling (throws BusFullException)
 * - Unit 4: Collections (Set / HashSet), Serialization (Serializable)
 *
 * Represents a college bus managed by the transport department.
 */
public class Bus implements Trackable, Serializable {
    private static final long serialVersionUID = 1L;

    private String busId;
    private String busNumber;
    private int capacity;
    private int routeId;
    private String driverId;
    private String currentLocation;
    private String status; // "On Time", "Delayed", "In Transit", "Reached Campus", "Maintenance"
    private Set<String> registeredStudentIds; // Unit 4: HashSet to prevent duplicate registrations

    public Bus() {
        this.registeredStudentIds = new HashSet<>();
        this.status = "Idle";
        this.currentLocation = "Campus Bus Depot";
    }

    public Bus(String busId, String busNumber, int capacity, int routeId, String driverId) {
        this.busId = busId;
        this.busNumber = busNumber;
        this.capacity = capacity;
        this.routeId = routeId;
        this.driverId = driverId;
        this.registeredStudentIds = new HashSet<>();
        this.status = "On Time";
        this.currentLocation = "Main Campus Gate";
    }

    // Unit 3: Exception Handling method
    public synchronized void registerStudent(String studentId) throws BusFullException {
        if (registeredStudentIds.contains(studentId)) {
            return; // Already registered
        }
        if (isFull()) {
            // Unit 3: Throw user-defined exception
            throw new BusFullException(busNumber, capacity);
        }
        registeredStudentIds.add(studentId);
    }

    public synchronized boolean unregisterStudent(String studentId) {
        return registeredStudentIds.remove(studentId);
    }

    public boolean isFull() {
        return registeredStudentIds.size() >= capacity;
    }

    public int getAvailableSeats() {
        return Math.max(0, capacity - registeredStudentIds.size());
    }

    public int getCurrentOccupancy() {
        return registeredStudentIds.size();
    }

    // Unit 2: Trackable interface implementation
    @Override
    public String getCurrentLocation() {
        return currentLocation;
    }

    @Override
    public void updateLocation(String newLocation) {
        this.currentLocation = newLocation;
    }

    @Override
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("%s (Seats: %d/%d) - Status: %s - Location: %s",
                busNumber, getCurrentOccupancy(), capacity, status, currentLocation);
    }

    // Getters and Setters
    public String getBusId() {
        return busId;
    }

    public void setBusId(String busId) {
        this.busId = busId;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public Set<String> getRegisteredStudentIds() {
        return registeredStudentIds;
    }

    public void setRegisteredStudentIds(Set<String> registeredStudentIds) {
        this.registeredStudentIds = registeredStudentIds;
    }
}
