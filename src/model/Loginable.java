package model;

/**
 * CS23304 - Unit 2: Interfaces
 * Loginable interface defines the contract for any system entity
 * that requires credential authentication and role verification.
 */
public interface Loginable {
    boolean authenticate(String password);
    String getRole();
}
