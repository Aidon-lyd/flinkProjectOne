package collectLog

import java.util.Properties

import constant.PayConstant
import org.apache.kafka.clients.producer.KafkaProducer
import utils.KafkaUtil

import scala.io.{BufferedSource, Source}

object CollectLogToKafka {
  def main(args: Array[String]): Unit = {
    val pro = new Properties()
    val producer: KafkaProducer[String, String] = KafkaUtil.getKafkaProducer(pro)
    val source: BufferedSource = Source.fromFile(PayConstant.CMCC_LOG_PATH,"UTF-8")
    val lines: Iterator[String] = source.getLines()
    while (lines.hasNext){
      Thread.sleep(1000)
      println(lines.next())
    }
    source.close()
    KafkaUtil.close(producer)
  }
}
