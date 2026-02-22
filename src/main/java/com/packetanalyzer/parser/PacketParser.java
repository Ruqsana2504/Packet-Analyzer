package com.packetanalyzer.parser;

import com.packetanalyzer.dpi.DpiEngine;
import com.packetanalyzer.model.PacketInfo;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;

import java.util.List;

public class PacketParser {

    private final DpiEngine dpiEngine;

    public PacketParser(DpiEngine dpiEngine) {
        this.dpiEngine = dpiEngine;
    }

    public void parse(Packet packet) {

        IpV4Packet ipPacket = packet.get(IpV4Packet.class);
        if (ipPacket == null) return;

        PacketInfo info = new PacketInfo();
        info.srcIp = ipPacket.getHeader().getSrcAddr().getHostAddress();
        info.dstIp = ipPacket.getHeader().getDstAddr().getHostAddress();

        if (packet.contains(TcpPacket.class)) {
            TcpPacket tcp = packet.get(TcpPacket.class);
            info.protocol = "TCP";
            info.srcPort = tcp.getHeader().getSrcPort().valueAsInt();
            info.dstPort = tcp.getHeader().getDstPort().valueAsInt();
            info.payload = tcp.getPayload() != null ?
                    tcp.getPayload().getRawData() : new byte[0];
        }

        dpiEngine.processPackets(List.of(info));
    }
}