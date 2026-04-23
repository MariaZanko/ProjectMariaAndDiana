package ua.university.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.*;

public class AutoSaveService {
    private static final Logger log = LoggerFactory.getLogger(AutoSaveService.class);
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(
            r -> { Thread t = new Thread(r, "auto-save"); t.setDaemon(true); return t; });
    private final PersistenceService persistence;

    public AutoSaveService(PersistenceService persistence) {
        this.persistence = persistence;
    }

    public void start(long intervalSeconds) {
        scheduler.scheduleAtFixedRate(() -> {
            log.info("Auto-save triggered");
            persistence.saveAll();
        }, intervalSeconds, intervalSeconds, TimeUnit.SECONDS);
        log.info("Auto-save started, interval={}s", intervalSeconds);
    }

    public void stop() {
        scheduler.shutdownNow();
        log.info("Auto-save stopped");
    }
}
