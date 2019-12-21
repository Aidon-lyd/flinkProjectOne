package app

import java.lang
import java.text.SimpleDateFormat
import java.util.Properties

import com.alibaba.fastjson.{JSON, JSONObject}
import org.apache.flink.api.common.functions.{AggregateFunction, FilterFunction}
import org.apache.flink.api.java.aggregation.AggregationFunction
import org.apache.flink.api.java.tuple.Tuple
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor
import org.apache.flink.streaming.api.functions.windowing.{AllWindowFunction, ProcessAllWindowFunction, WindowFunction}
import org.apache.flink.streaming.api.functions.{AssignerWithPeriodicWatermarks, AssignerWithPunctuatedWatermarks}
import org.apache.flink.streaming.api.{CheckpointingMode, TimeCharacteristic}
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.watermark.Watermark
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.{GlobalWindow, TimeWindow}
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.flink.streaming.util.serialization.SimpleStringSchema
import org.apache.flink.util.Collector
import utils.NumFormatUtil

object orderTotal {
  def main(args: Array[String]): Unit = {
    val pro = new Properties()
    pro.put("bootstrap.servers", "hadoop05:9092,hadoop06:9092,hadoop07:9092")
    pro.put("group.id", "test")
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    env.enableCheckpointing(1000)
    env.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE)

    val source: DataStream[String] = env.addSource(
      new FlinkKafkaConsumer[String]("test", new SimpleStringSchema(), pro)
    )
    //val order_source: DataStream[order] =
    source.map(x => {
      val json: JSONObject = JSON.parseObject(x)
      val serviceName: String = json.get("serviceName").toString
      val bussinessRst: String = json.get("bussinessRst").toString
      val chargefee: Double = NumFormatUtil.toDouble(json.get("chargefee").toString)
      val requestId: String = json.get("requestId").toString
      val receiveNotifyTime: String = json.get("receiveNotifyTime").toString
      val provinceCode: String = json.get("provinceCode").toString
      order(serviceName, bussinessRst, chargefee, requestId, receiveNotifyTime, provinceCode)
    }).assignTimestampsAndWatermarks(new myAssign)
      .filter(new myFilter)
      //.keyBy("")
      .timeWindowAll(Time.hours(1),Time.minutes(1))
      .sum("chargefee")
      //.aggregate(new AggData,new myWindowFunction)
      .print()

    env.execute("order per minutes")

  }
}
case class order(serviceName:String,bussinessRst:String,chargefee:Double,requestId:String,receiveNotifyTime:String,provinceCode:String)

class myAssign extends BoundedOutOfOrdernessTimestampExtractor[order](Time.seconds(1)){
  override def extractTimestamp(t: order): Long = {
    val newtime :Long= new SimpleDateFormat("yyyyMMddHHmmss").parse(t.receiveNotifyTime).getTime
    newtime
  }
}
class myFilter extends FilterFunction[order]{
  override def filter(t: order): Boolean = {
    t.bussinessRst == "reChargeNotifyReq"
  }
}

class AggData extends AggregateFunction[order , ( Long, Double), ( Long, Double) ]{
  override def createAccumulator(): (Long, Double) = (0l , 0l)

  override def add(in: order, acc: (Long, Double)): (Long, Double) = {
    (acc._1+1,acc._2 + in.chargefee)
  }

  override def getResult(acc: (Long, Double)): (Long, Double) = {
    acc
  }

  override def merge(acc: (Long, Double), acc1: (Long, Double)): (Long, Double) = {
    (acc._1+acc1._1 , acc._2+acc1._2)
  }
}

class myWindowFunction extends AllWindowFunction[(Long, Double),result,TimeWindow]{
//  override def apply(key: Tuple, window: TimeWindow, input: lang.Iterable[(Long, Double)], out: Collector[result]): Unit = {
//    val in = input.iterator().next()
//    out.collect(result(in._1, in._2))
//  }
  override def apply(window: TimeWindow, values: lang.Iterable[(Long, Double)], out: Collector[result]): Unit = {
    val in = values.iterator().next()
    out.collect(result(in._1, in._2))
  }
}

case class result(count:Long , sum:Double)