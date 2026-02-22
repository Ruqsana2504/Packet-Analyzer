package com.packetanalyzer.sni;

public class SniExtractor {

    public String extractSni(byte[] payload) {
        // Simplified TLS ClientHello parser stub
        if (payload == null || payload.length < 5)
            return null;

        return "example.com"; // Placeholder logic
    }
}