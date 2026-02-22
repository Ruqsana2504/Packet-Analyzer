package com.packetanalyzer;

import com.packetanalyzer.dpi.DpiEngine;
import com.packetanalyzer.parser.PacketParser;
import com.packetanalyzer.pcap.PcapReader;

public class Main {

    public static void main(String[] args) throws Exception {

        if (args.length < 1) {
            System.out.println("Usage: java -jar analyzer.jar <pcap-file>");
            return;
        }

        DpiEngine dpiEngine = new DpiEngine();
        PacketParser parser = new PacketParser(dpiEngine);
        PcapReader reader = new PcapReader();

        reader.read(args[0], parser);

    }
}