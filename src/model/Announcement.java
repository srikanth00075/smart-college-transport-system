package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * CS23304 Units Covered:
 * - Unit 1: Date/Time API (LocalDateTime)
 * - Unit 4: Serialization
 *
 * Represents an announcement broadcast by Admin to Students or Drivers.
 */
public class Announcement implements Serializable {
    private static final long serialVersionUID = 1L;

    private static int annCounter = 100;

    private String id;
    private String title;
    private String message;
    private String sender;
    private String targetAudience; // "All", "Students", "Drivers"
    private LocalDateTime timestamp;

    public Announcement() {
        this.id = "ANN-" + (++annCounter);
        this.timestamp = LocalDateTime.now();
        this.targetAudience = "All";
    }

    public Announcement(String title, String message, String sender, String targetAudience) {
        this.id = "ANN-" + (++annCounter);
        this.title = title;
        this.message = message;
        this.sender = sender;
        this.targetAudience = targetAudience;
        this.timestamp = LocalDateTime.now();
    }

    public Announcement(String id, String title, String message, String sender,
                        String targetAudience, LocalDateTime timestamp) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.sender = sender;
        this.targetAudience = targetAudience;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a");
        return String.format("[%s] %s (To: %s by %s): %s",
                timestamp.format(fmt), title, targetAudience, sender, message);
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTargetAudience() {
        return targetAudience;
    }

    public void setTargetAudience(String targetAudience) {
        this.targetAudience = targetAudience;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
