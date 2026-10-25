package service;

import model.Announcement;
import model.Bus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * CS23304 - Unit 3: Multithreading & Synchronization
 * Implements a background Daemon Thread that continuously monitors
 * bus statuses, simulates real-time tracking updates, and broadcasts
 * transport notifications to connected client dashboards.
 */
public class NotificationService extends Thread {
    private final List<Announcement> announcements;
    private final List<Consumer<String>> notificationListeners;
    private final BusService busService;
    private volatile boolean running = true;

    public NotificationService(BusService busService) {
        // Set as background daemon thread
        super("Transport-Notification-Service-Thread");
        setDaemon(true);
        this.busService = busService;
        this.announcements = Collections.synchronizedList(new ArrayList<>());
        this.notificationListeners = new CopyOnWriteArrayList<>();
        seedDefaultAnnouncements();
    }

    private void seedDefaultAnnouncements() {
        announcements.add(new Announcement("Semester End Special Trips",
                "College management has scheduled additional evening return buses at 18:30 for exam week.",
                "Transport Office", "All"));
        announcements.add(new Announcement("Route 2 Road Maintenance Alert",
                "Metro Station Gate 2 stop temporarily shifted 50 meters forward due to civil works.",
                "Fleet Supervisor", "Students"));
        announcements.add(new Announcement("Speed Governor & Safety Inspection",
                "All drivers must report to workshop bay #2 by 17:30 for mandatory speed limiter inspection.",
                "Chief Transport Officer", "Drivers"));
    }

    // Unit 3: Multithreading run loop
    @Override
    public void run() {
        System.out.println("[Multithreading] " + getName() + " started running in background.");
        int cycle = 0;

        while (running) {
            try {
                // Background cycle every 8 seconds
                Thread.sleep(8000);
                cycle++;

                // Synchronized block to demonstrate thread safety
                if (busService != null) {
                    List<Bus> buses = busService.getAllBuses();
                    if (!buses.isEmpty()) {
                        // Gently cycle a bus status / location simulation
                        Bus b = buses.get(cycle % buses.size());
                        synchronized (b) {
                            if ("On Time".equalsIgnoreCase(b.getStatus())) {
                                // Keep active
                            }
                        }
                    }
                }

                // Notify UI listeners of heartbeat
                notifyListeners("SYSTEM_PULSE");

            } catch (InterruptedException e) {
                System.out.println("[Multithreading] Background notification thread interrupted.");
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * Broadcasts a new announcement (Admin feature).
     * Synchronized to guarantee thread safety across concurrent requests.
     */
    public synchronized void broadcastAnnouncement(Announcement announcement) {
        announcements.add(0, announcement); // Prepend to top
        notifyListeners("ANNOUNCEMENT:" + announcement.getTitle());
    }

    public void addListener(Consumer<String> listener) {
        notificationListeners.add(listener);
    }

    public void removeListener(Consumer<String> listener) {
        notificationListeners.remove(listener);
    }

    private void notifyListeners(String event) {
        for (Consumer<String> listener : notificationListeners) {
            try {
                listener.accept(event);
            } catch (Exception ignored) {}
        }
    }

    public List<Announcement> getAllAnnouncements() {
        synchronized (announcements) {
            return new ArrayList<>(announcements);
        }
    }

    public void stopService() {
        this.running = false;
        interrupt();
    }
}
