package com.job;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.pcap4j.core.*;
import org.pcap4j.packet.UdpPacket;

import java.util.Properties;

public class PcapToKafka {

    private static Config config = ConfigFactory.load();
    private static final String PCAP_FILE_READ = config.getString("data.source");

    public static void main(String[] args) throws PcapNativeException, NotOpenException, InterruptedException {
        PcapHandle handleRead = Pcaps.openOffline(PCAP_FILE_READ);
        Producer<String, byte[]> producer = new KafkaProducer<>(getKafkaProperties());
        PacketListener listener = fullPacket -> {
            UdpPacket udpPacket = fullPacket.get(UdpPacket.class);
            if (udpPacket != null) {
                byte[] bytes = udpPacket.getRawData();
                byte[] IPFIX = new byte[bytes.length - 8];
                System.arraycopy(bytes, 8, IPFIX, 0, bytes.length - 8);
                producer.send(new ProducerRecord<>(config.getString("kafka.topic.out"), null, IPFIX));
            }
        };

        handleRead.loop(-1, listener);

        producer.close();
        handleRead.close();
    }

    private static Properties getKafkaProperties() {
        Properties props = new Properties();
        props.put("bootstrap.servers", config.getString("kafka.bootstrap.servers"));
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
        return props;
    }
}
