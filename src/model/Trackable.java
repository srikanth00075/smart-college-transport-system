package model;

/**
 * CS23304 - Unit 2: Interfaces
 * Trackable interface provides standard contract for entities with dynamic
 * geolocation or route progress tracking (e.g., Buses, Students).
 */
public interface Trackable {
    String getCurrentLocation();
    void updateLocation(String newLocation);
    String getStatus();
}
