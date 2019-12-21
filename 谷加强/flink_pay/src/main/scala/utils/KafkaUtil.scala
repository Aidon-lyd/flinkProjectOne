package utils



import java.util.Properties

import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer

object KafkaUtil {
  def getKafkaProducer(pro:Properties )={
    pro.put("bootstrap.servers", "hadoop05:9092,hadoop06:9092,hadoop07:9092")
    pro.put("key.serializer",
      "org.apache.kafka.common.serialization.StringSerializer")
    pro.put("value.serializer",
      "org.apache.kafka.common.serialization.StringSerializer")
    val producer = new KafkaProducer[String,String](pro)
    producer
  }

  def close(producer:KafkaProducer[String,String])={
    if(producer!=null)
      producer.close()
  }
}
