package cn.cafuc.cs.zzy.function

import cn.cafuc.cs.zzy.model.PayData
import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector

/**
  * @Author : zzy
  * @Date : 2019/12/21
  * @Verson : 1.0
  */
class PayProcess() extends ProcessWindowFunction[PayData, (Long, Double, Long), String, TimeWindow] {
  override def process(key: String, context: Context, elements: Iterable[PayData], out: Collector[(Long, Double, Long)]): Unit = {
    var reslut: (Long, Double, Long) = (0L, 0.0, 0L)
    for (payData <- elements) {
      if (payData.getBussinessRst == "0000") {
        reslut = (reslut._1 + 1, reslut._2 + payData.getChargefee, reslut._3 + 1)
      } else {
        reslut = (reslut._1 + 1, reslut._2, reslut._3)
      }
    }
    // 这里还可以算一些每个小时的成功率
    out.collect(reslut)
  }
}
