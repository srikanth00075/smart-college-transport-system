package exception;

/**
 * CS23304 - Unit 3: User-defined Exception Handling
 * Thrown when a student attempts to register for a bus that has reached
 * its maximum seat capacity.
 */
public class BusFullException extends Exception {
    private static final long serialVersionUID = 1L;

    private String busNumber;
    private int capacity;

    public BusFullException(String message) {
        super(message);
    }

    public BusFullException(String busNumber, int capacity) {
        super(String.format("Sorry! Bus %s is full (All %d seats are occupied).", busNumber, capacity));
        this.busNumber = busNumber;
        this.capacity = capacity;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public int getCapacity() {
        return capacity;
    }
}
