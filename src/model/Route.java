package model;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * CS23304 Units Covered:
 * - Unit 1: Date/Time API (LocalTime)
 * - Unit 4: Collections Framework (List, ArrayList), Serialization
 *
 * Represents a predefined transport route with scheduled stops and timings.
 */
public class Route implements Serializable {
    private static final long serialVersionUID = 1L;

    private int routeId;
    private String routeName;
    private String startingPoint;
    private String destination;
    private List<String> stops; // Unit 4: ArrayList
    private LocalTime morningStartTime; // Unit 1: Date/Time API
    private LocalTime eveningReturnTime;

    public Route() {
        this.stops = new ArrayList<>();
        this.morningStartTime = LocalTime.of(7, 30);
        this.eveningReturnTime = LocalTime.of(16, 45);
    }

    public Route(int routeId, String routeName, String startingPoint, String destination,
                 LocalTime morningStartTime, LocalTime eveningReturnTime) {
        this.routeId = routeId;
        this.routeName = routeName;
        this.startingPoint = startingPoint;
        this.destination = destination;
        this.stops = new ArrayList<>();
        this.morningStartTime = morningStartTime;
        this.eveningReturnTime = eveningReturnTime;
    }

    public void addStop(String stop) {
        if (stop != null && !stop.trim().isEmpty() && !stops.contains(stop.trim())) {
            stops.add(stop.trim());
        }
    }

    public boolean hasStop(String stop) {
        if (stop == null) return false;
        return stops.stream().anyMatch(s -> s.equalsIgnoreCase(stop.trim()));
    }

    public String getStopsFormatted() {
        return String.join(" -> ", stops);
    }

    @Override
    public String toString() {
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("hh:mm a");
        return String.format("Route %d: %s (%s to %s | Morning: %s | Return: %s)",
                routeId, routeName, startingPoint, destination,
                morningStartTime.format(timeFmt), eveningReturnTime.format(timeFmt));
    }

    // Getters and Setters
    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getStartingPoint() {
        return startingPoint;
    }

    public void setStartingPoint(String startingPoint) {
        this.startingPoint = startingPoint;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public List<String> getStops() {
        return stops;
    }

    public void setStops(List<String> stops) {
        this.stops = stops;
    }

    public LocalTime getMorningStartTime() {
        return morningStartTime;
    }

    public void setMorningStartTime(LocalTime morningStartTime) {
        this.morningStartTime = morningStartTime;
    }

    public LocalTime getEveningReturnTime() {
        return eveningReturnTime;
    }

    public void setEveningReturnTime(LocalTime eveningReturnTime) {
        this.eveningReturnTime = eveningReturnTime;
    }
}
