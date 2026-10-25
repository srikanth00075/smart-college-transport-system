package service;

import exception.InvalidRouteException;
import model.Route;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CS23304 Units Covered:
 * - Unit 2: Method Overloading (searchRoute variations)
 * - Unit 3: User-defined Exception Handling (InvalidRouteException)
 * - Unit 4: Collections Framework (ArrayList, HashMap), Lambda Expressions
 */
public class RouteService {
    private Map<Integer, Route> routeMap;

    public RouteService() {
        this.routeMap = new LinkedHashMap<>();
        seedDefaultRoutes();
    }

    private void seedDefaultRoutes() {
        // Route 1
        Route r1 = new Route(1, "Route 1 - South Central Line", "Green Park Colony", "College Campus",
                LocalTime.of(7, 15), LocalTime.of(16, 45));
        r1.addStop("Green Park Colony");
        r1.addStop("City Center Mall");
        r1.addStop("Tech Park Crossing");
        r1.addStop("College Campus");

        // Route 2
        Route r2 = new Route(2, "Route 2 - Metro Express", "Metro Pillar 140", "College Campus",
                LocalTime.of(7, 30), LocalTime.of(17, 0));
        r2.addStop("Metro Pillar 140");
        r2.addStop("Metro Station Gate 2");
        r2.addStop("Botanical Garden");
        r2.addStop("College Campus");

        // Route 3
        Route r3 = new Route(3, "Route 3 - North Suburb Ring", "North Hills Plaza", "College Campus",
                LocalTime.of(7, 0), LocalTime.of(16, 30));
        r3.addStop("North Hills Plaza");
        r3.addStop("Outer Ring Road Junction");
        r3.addStop("University Cross Road");
        r3.addStop("College Campus");

        routeMap.put(r1.getRouteId(), r1);
        routeMap.put(r2.getRouteId(), r2);
        routeMap.put(r3.getRouteId(), r3);
    }

    // ==========================================================
    // CS23304 - Unit 2: Method Overloading
    // ==========================================================

    /**
     * Overload #1: Search Route by Route ID (int parameter).
     */
    public Optional<Route> searchRoute(int routeId) {
        return Optional.ofNullable(routeMap.get(routeId));
    }

    /**
     * Overload #2: Search Route by query matching route name or any included stop (String parameter).
     * Unit 4: Demonstrates Stream & Lambda.
     */
    public List<Route> searchRoute(String nameOrStop) {
        if (nameOrStop == null || nameOrStop.trim().isEmpty()) {
            return getAllRoutes();
        }
        String q = nameOrStop.trim().toLowerCase();
        return routeMap.values().stream()
                .filter(r -> r.getRouteName().toLowerCase().contains(q) ||
                        r.getStartingPoint().toLowerCase().contains(q) ||
                        r.getDestination().toLowerCase().contains(q) ||
                        r.hasStop(q))
                .collect(Collectors.toList());
    }

    /**
     * Validates if a pickup stop belongs to a specific route.
     * Throws InvalidRouteException if invalid.
     */
    public void validateStopOnRoute(int routeId, String stopName) throws InvalidRouteException {
        Route route = routeMap.get(routeId);
        if (route == null) {
            throw new InvalidRouteException("Route ID " + routeId + " does not exist.");
        }
        if (!route.hasStop(stopName)) {
            throw new InvalidRouteException(stopName, route.getRouteName());
        }
    }

    public synchronized void addRoute(Route route) {
        if (route != null) {
            routeMap.put(route.getRouteId(), route);
        }
    }

    public synchronized boolean removeRoute(int routeId) {
        return routeMap.remove(routeId) != null;
    }

    public List<Route> getAllRoutes() {
        return new ArrayList<>(routeMap.values());
    }

    public Route getRouteById(int routeId) {
        return routeMap.get(routeId);
    }
}
