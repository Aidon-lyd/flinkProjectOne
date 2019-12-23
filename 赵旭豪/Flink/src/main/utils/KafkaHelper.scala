import java.util.Properties

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011

object KafkaHelper {


  val ZOOKEEPER_HOST = "hadoop111:2181"
  val KAFKA_BROKER = "hadoop111:9092"
  val TRANSACTION_GROUP = "flink-mobile"
  val TOPIC = "mobile"


  def getConsumer(): FlinkKafkaConsumer011[String] = {

    // configure Kafka consumer
    val kafkaProps = new Properties()
    kafkaProps.setProperty("zookeeper.connect", ZOOKEEPER_HOST)
    kafkaProps.setProperty("bootstrap.servers", KAFKA_BROKER)
    kafkaProps.setProperty("group.id", TRANSACTION_GROUP)

    //topicd的名字是new，schema默认使用SimpleStringSchema()即可
    val consumer: FlinkKafkaConsumer011[String] = new FlinkKafkaConsumer011[String](TOPIC, new SimpleStringSchema(), kafkaProps)
    consumer.setStartFromEarliest()
    consumer
  }



}
