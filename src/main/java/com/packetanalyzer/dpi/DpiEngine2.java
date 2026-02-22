package com.packetanalyzer.dpi;

import com.packetanalyzer.connection.ConnectionTracker;
import com.packetanalyzer.model.PacketInfo;
import com.packetanalyzer.rule.RuleManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DpiEngine2 {

    private final RuleManager ruleManager = new RuleManager();
    private final ConnectionTracker tracker = new ConnectionTracker();
    private final ExecutorService executor =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void processPacket(PacketInfo packet) {

        executor.submit(() -> {

            tracker.update(packet);

            boolean matched = ruleManager.matches(packet);

            printDetailedPacket(packet, matched);
        });
    }

    private void printDetailedPacket(PacketInfo packet, boolean alert) {

        String timestamp = LocalDateTime.now().format(formatter);

        String connection = packet.srcIp + ":" + packet.srcPort +
                " -> " +
                packet.dstIp + ":" + packet.dstPort;

        int payloadSize = packet.payload != null ? packet.payload.length : 0;

        System.out.println("--------------------------------------------------");
        System.out.println("Timestamp       : " + timestamp);
        System.out.println("Protocol        : " + packet.protocol);
        System.out.println("Connection      : " + connection);
        System.out.println("Payload Size    : " + payloadSize + " bytes");
        System.out.println("Active Sessions : " + tracker.getActiveConnections());

        if (alert) {
            System.out.println("STATUS          : ALERT ⚠ Rule Matched");
        } else {
            System.out.println("STATUS          : OK");
        }

        System.out.println("--------------------------------------------------\n");
    }

    public void shutdown() {
        executor.shutdown();
    }
}