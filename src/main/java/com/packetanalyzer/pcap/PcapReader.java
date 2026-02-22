package com.packetanalyzer.pcap;

import com.packetanalyzer.parser.PacketParser;
import org.pcap4j.core.*;
import org.pcap4j.packet.Packet;

public class PcapReader {

    public void read(String filePath, PacketParser parser) throws Exception {

        PcapHandle handle = Pcaps.openOffline(filePath);

        Packet packet;
        while ((packet = handle.getNextPacket()) != null) {
            parser.parse(packet);
        }

        handle.close();
    }
}