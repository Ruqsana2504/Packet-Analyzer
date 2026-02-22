package com.packetanalyzer.rule;

import com.packetanalyzer.model.PacketInfo;
import java.util.ArrayList;
import java.util.List;

public class RuleManager {

    private final List<String> blockedIps = new ArrayList<>();

    public RuleManager() {
        blockedIps.add("192.168.1.100");
    }

    public boolean matches(PacketInfo packet) {
        return blockedIps.contains(packet.dstIp);
    }
}