package com.packetanalyzer.connection;

import java.util.concurrent.ConcurrentHashMap;
import com.packetanalyzer.model.PacketInfo;

public class ConnectionTracker {

    private final ConcurrentHashMap<String, Long> connections = new ConcurrentHashMap<>();

    public void update(PacketInfo packet) {
        connections.put(packet.getConnectionKey(), System.currentTimeMillis());
    }

    public int getActiveConnections() {
        return connections.size();
    }
}