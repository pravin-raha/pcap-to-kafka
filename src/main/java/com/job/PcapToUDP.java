package com.job;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.pcap4j.core.*;
import org.pcap4j.packet.UdpPacket;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class PcapToUDP {
    private static Config config = ConfigFactory.load();
    private static final String PCAP_FILE_READ = config.getString("data.source");

    public static void main(String[] args) throws PcapNativeException, NotOpenException, InterruptedException, SocketException {
        PcapHandle handleRead = Pcaps.openOffline(PCAP_FILE_READ);
        DatagramSocket ds = new DatagramSocket();
        String ip = config.getString("udp.ip");
        int port = config.getInt("udp.port");
        PacketListener listener = fullPacket -> {
            UdpPacket udpPacket = fullPacket.get(UdpPacket.class);
            if (udpPacket != null) {
                byte[] bytes = udpPacket.getRawData();
                byte[] IPFIX = new byte[bytes.length - 8];
                System.arraycopy(bytes, 8, IPFIX, 0, bytes.length - 8);

                try {
                    DatagramPacket  DpSend = new DatagramPacket(IPFIX, IPFIX.length, InetAddress.getByName(ip), port);
                    ds.send(DpSend);
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            }
        };

        handleRead.loop(-1, listener);

        handleRead.close();
    }
}
