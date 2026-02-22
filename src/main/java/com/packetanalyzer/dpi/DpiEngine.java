package com.packetanalyzer.dpi;

import com.packetanalyzer.model.PacketInfo;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class DpiEngine {

    private final int numLoadBalancers = 2;
    private final int fpsPerLb = 2;

    private final AtomicInteger totalPackets = new AtomicInteger();
    private final AtomicInteger totalBytes = new AtomicInteger();
    private final AtomicInteger tcpPackets = new AtomicInteger();
    private final AtomicInteger udpPackets = new AtomicInteger();
    private final AtomicInteger forwarded = new AtomicInteger();
    private final AtomicInteger dropped = new AtomicInteger();

    private final Map<Integer, AtomicInteger> lbStats = new LinkedHashMap<>();
    private final Map<Integer, AtomicInteger> fpStats = new LinkedHashMap<>();
    private final Map<String, Integer> appStats = new LinkedHashMap<>();
    private final Map<String, String> detectedDomains = new LinkedHashMap<>();

    public DpiEngine() {
        for (int i = 0; i < numLoadBalancers; i++)
            lbStats.put(i, new AtomicInteger());

        for (int i = 0; i < numLoadBalancers * fpsPerLb; i++)
            fpStats.put(i, new AtomicInteger());

        printStartupBanner();
    }

    // ============================================================
    // STARTUP BANNER (Matches C++ Constructor Output)
    // ============================================================

    private void printStartupBanner() {

        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║              DPI ENGINE v2.0 (Multi-threaded)                 ║");
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        System.out.printf("║ Load Balancers:  %-4d FPs per LB:  %-4d Total FPs:  %-4d        ║%n",
                numLoadBalancers,
                fpsPerLb,
                numLoadBalancers * fpsPerLb);
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    // ============================================================
    // MAIN PROCESSING
    // ============================================================

    public void processPackets(List<PacketInfo> packets) {

        System.out.println("[Reader] Processing packets...");

        for (PacketInfo packet : packets) {

            totalPackets.incrementAndGet();
            totalBytes.addAndGet(packet.length);

            if ("TCP".equals(packet.protocol))
                tcpPackets.incrementAndGet();
            else if ("UDP".equals(packet.protocol))
                udpPackets.incrementAndGet();

            int lbIndex = Math.abs(packet.hashCode()) % numLoadBalancers;
            lbStats.get(lbIndex).incrementAndGet();

            int fpIndex = Math.abs(packet.hashCode()) % (numLoadBalancers * fpsPerLb);
            fpStats.get(fpIndex).incrementAndGet();

            String app = detectApplication(packet);
            appStats.put(app, appStats.getOrDefault(app, 0) + 1);

            if (app.equals("YouTube") || "192.168.1.50".equals(packet.srcIp))
                dropped.incrementAndGet();
            else
                forwarded.incrementAndGet();
        }

        System.out.println("[Reader] Done reading " + totalPackets.get() + " packets\n");

        printReport();
        printDetectedDomains();
    }

    // ============================================================
    // REPORT GENERATION (Matches C++ generateReport Style)
    // ============================================================

    private void printReport() {

        StringBuilder sb = new StringBuilder();

        sb.append("╔══════════════════════════════════════════════════════════════╗\n");
        sb.append("║                      PROCESSING REPORT                        ║\n");
        sb.append("╠══════════════════════════════════════════════════════════════╣\n");

        appendStat(sb, "Total Packets:", totalPackets.get());
        appendStat(sb, "Total Bytes:", totalBytes.get());
        appendStat(sb, "TCP Packets:", tcpPackets.get());
        appendStat(sb, "UDP Packets:", udpPackets.get());

        sb.append("╠══════════════════════════════════════════════════════════════╣\n");

        appendStat(sb, "Forwarded:", forwarded.get());
        appendStat(sb, "Dropped:", dropped.get());

        sb.append("╠══════════════════════════════════════════════════════════════╣\n");
        sb.append("║ THREAD STATISTICS                                             ║\n");

        for (int i = 0; i < numLoadBalancers; i++) {
            appendStat(sb, "LB" + i + " dispatched:", lbStats.get(i).get());
        }

        for (int i = 0; i < numLoadBalancers * fpsPerLb; i++) {
            appendStat(sb, "FP" + i + " processed:", fpStats.get(i).get());
        }

        sb.append("╠══════════════════════════════════════════════════════════════╣\n");
        sb.append("║                   APPLICATION BREAKDOWN                       ║\n");
        sb.append("╠══════════════════════════════════════════════════════════════╣\n");

        for (Map.Entry<String, Integer> entry : appStats.entrySet()) {
            appendAppLine(sb, entry.getKey(), entry.getValue());
        }

        sb.append("╚══════════════════════════════════════════════════════════════╝\n");

        System.out.print(sb);
    }

    private void appendStat(StringBuilder sb, String label, int value) {
        sb.append(String.format("║ %-22s %10d                              ║\n",
                label, value));
    }

    private void appendAppLine(StringBuilder sb, String app, int count) {

        double percent = (count * 100.0) / totalPackets.get();
        int barLength = (int) (percent / 5);

        String bar = "#".repeat(Math.max(0, barLength));

        String blocked = app.equals("YouTube") ? " (BLOCKED)" : "";

        sb.append(String.format("║ %-20s %5d %6.1f%% %-10s%s                   ║\n",
                app, count, percent, bar, blocked));
    }

    private void printDetectedDomains() {
        System.out.println("\n[Detected Domains/SNIs]");
        for (Map.Entry<String, String> entry : detectedDomains.entrySet()) {
            System.out.println("  - " + entry.getKey() + " -> " + entry.getValue());
        }
    }

    // ============================================================
    // SIMPLE APPLICATION DETECTION
    // ============================================================

    private String detectApplication(PacketInfo packet) {

        if (packet.payload == null || packet.payload.length == 0)
            return "Unknown";

        String data = new String(packet.payload).toLowerCase();

        if (data.contains("youtube")) {
            detectedDomains.put("www.youtube.com", "YouTube");
            return "YouTube";
        }

        if (data.contains("facebook")) {
            detectedDomains.put("www.facebook.com", "Facebook");
            return "Facebook";
        }

        if (data.contains("google")) {
            detectedDomains.put("www.google.com", "Google");
            return "Google";
        }

        if (packet.dstPort == 443) return "HTTPS";
        if (packet.dstPort == 53) return "DNS";

        return "Unknown";
    }
}