package com.packetanalyzer.model;

public class PacketInfo {

    public String srcIp;
    public String dstIp;
    public int srcPort;
    public int dstPort;
    public String protocol;
    public byte[] payload;
    public int length;

    public String getConnectionKey() {
        return srcIp + ":" + srcPort + "-" + dstIp + ":" + dstPort;
    }
}