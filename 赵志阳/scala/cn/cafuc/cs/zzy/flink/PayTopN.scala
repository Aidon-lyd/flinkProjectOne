package cn.cafuc.cs.zzy.flink

import java.io.{BufferedReader, FileInputStream, InputStreamReader}

import cn.cafuc.cs.zzy.model.PayData
import com.alibaba.fastjson.JSON
import org.apache.flink.api.scala.{DataSet, ExecutionEnvironment}
import org.apache.flink.core.fs.FileSystem.WriteMode
import org.apache.flink.streaming.api.scala._


/**
  *
  * 省份前十
  *
  * 实时思路：
  *   开个窗口中间的聚合数据放入redis中。
  *   窗口关闭时取redis中的数据求topN再写入Mysql
  *
  * @Author : zzy
  * @Date : 2019/12/21
  * @Verson : 1.0
  */
object PayTopN {
  def main(args: Array[String]): Unit = {

    //1. 获取环境
    val env = ExecutionEnvironment.getExecutionEnvironment

    val reader = new BufferedReader(new InputStreamReader(new FileInputStream("dir/in/city.txt"), "UTF-8"))
    var cityMap: Map[Int, String] = Map()
    var line: String = ""
    while (reader.ready() && (line = reader.readLine()) != null) {
      val kv: Array[String] = line.split("\\s+")
      cityMap += (kv(0).toInt -> kv(1))
    }
    import org.apache.flink.api.scala._

    val payRecordDataSet: DataSet[(String, Int)] = env.readTextFile("dir/in/cmcc.json")
      .map(line => JSON.parseObject(line, classOf[PayData]))
      .map(payData => {
        var cnt = 0
        if (payData.getBussinessRst == "0000") cnt = 1
        else cnt = 0
        (cityMap.getOrElse(payData.getProvinceCode, "未知"), cnt)
      })

    val res: AggregateDataSet[(String, Int)] = payRecordDataSet
      .groupBy(0)
      .sum(1)
    //没错  离线没做出来topN
    res.writeAsText("dir/out", WriteMode.OVERWRITE).setParallelism(1)
  }

}
