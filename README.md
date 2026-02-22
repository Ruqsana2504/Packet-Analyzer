🧠 Overview

This project is a multi-threaded Packet Analyzer with Deep Packet Inspection (DPI) implemented in Java using pcap4j.

It:

Reads packets from a .pcap file

Parses IP/TCP layers

Tracks active connections

Applies rule-based inspection

Generates alerts for matching traffic

📦 Project Structure
packet-analyzer-java/
│
├── pom.xml
└── src/main/java/com/packetanalyzer/
├── Main.java
├── pcap/PcapReader.java
├── parser/PacketParser.java
├── connection/ConnectionTracker.java
├── dpi/DpiEngine.java
├── rule/RuleManager.java
├── model/PacketInfo.java
✅ Requirements

Make sure the following are installed:

Java 17 or higher

Maven 3.8+

Npcap (Windows) OR libpcap (Linux/macOS)

🔹 Install libpcap (Linux)
sudo apt update
sudo apt install libpcap-dev
🔹 Install Npcap (Windows)

Download and install from:

https://nmap.org/npcap/

During installation, enable:

✔ Install Npcap in WinPcap API-compatible mode

🔨 Build the Project

From the project root directory:

mvn clean install

After building, the JAR file will be created inside:

target/packetanalyzer-1.0-SNAPSHOT.jar

▶️ How to Run
Option 1 — Run Using Maven
mvn exec:java -Dexec.mainClass="com.packetanalyzer.Main" -Dexec.args="sample.pcap"

Option 2 — Run Using the JAR
java -jar target/packetanalyzer-1.0-SNAPSHOT.jar sample.pcap

Example output:

--------------------------------------------------
Timestamp       : 2026-02-22 01:57:44
Protocol        : TCP
Connection      : 192.168.1.5:51544 -> 192.168.1.100:443
Payload Size    : 0 bytes
Active Sessions : 1
STATUS          : ALERT ? Rule Matched
--------------------------------------------------

🔍 What the Program Does

Opens a PCAP file
Extracts IPv4 packets
Parses TCP layer
Extracts payload
Tracks connections
Applies rule matching
Logs alerts

⚙️ Multi-Threading

The DPI engine automatically scales to available CPU cores:
Runtime.getRuntime().availableProcessors()
This allows parallel packet inspection for improved performance.