package com.packetanalyzer.dpi;

import com.packetanalyzer.connection.ConnectionTracker;
import com.packetanalyzer.model.PacketInfo;
import com.packetanalyzer.rule.RuleManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DpiEngine1 {

    private final RuleManager ruleManager = new RuleManager();
    private final ConnectionTracker tracker = new ConnectionTracker();
    private final ExecutorService executor =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public void processPacket(PacketInfo packet) {

        executor.submit(() -> {
            tracker.update(packet);

            if (ruleManager.matches(packet)) {
                System.out.println("⚠ ALERT: Rule matched for connection "
                        + packet.getConnectionKey());
            }
        });
    }

    public void shutdown() {
        executor.shutdown();
    }
}