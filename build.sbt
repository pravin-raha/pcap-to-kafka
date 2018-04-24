name := "pcap-to-kafka"

version := "0.1"

scalaVersion := "2.12.5"

libraryDependencies += "org.pcap4j" % "pcap4j-core" % "1.1.0"
libraryDependencies += "org.pcap4j" % "pcap4j-packetfactory-static" % "1.1.0"
libraryDependencies += "org.apache.kafka" %% "kafka" % "1.1.0"
libraryDependencies += "org.apache.kafka" % "kafka-clients" % "1.1.0"
libraryDependencies += "com.typesafe" % "config" % "1.3.3"