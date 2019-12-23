import com.alibaba.fastjson.JSON
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.scala.DataStream
import org.apache.flink.streaming.api.windowing.time.Time

/**
  *
  * 需求一：
  *        统计全网的充值订单量, 充值金额, 充值成功数
  *        实时充值业务办理趋势, 主要统计全网的订单量数据
  */
object Statistical1 {

  def main(args: Array[String]): Unit = {

    //获取配置对象
    val env = FlinkHelper.getEnv
    //从kafka中消费
    val consumer = KafkaHelper.getConsumer()
    val date = env.addSource(consumer)
    date.print()
    //将获取的json文本进行解析
    val base_data: DataStream[(String, String, Int, Int, BigDecimal, Long)] = date
      .filter(
        values =>
          JSON
            .parseObject(values)
            .getJSONObject("serviceName")
            .equals("reChargeNotifyReq")
      )
      .map(line => {
        val serviceName: String =
          JSON.parseObject(line).getString("serviceName")
        val bussinessRst: String =
          JSON.parseObject(line).getString("bussinessRst")
        val chargefee1: Double = JSON.parseObject(line).getDouble("chargefee")
        val requestId: String = JSON.parseObject(line).getString("requestId")
        val receiveNotifyTime: String =
          JSON.parseObject(line).getString("receiveNotifyTime")
        val provinceCode: Int =
          JSON.parseObject(line).getInteger("provinceCode")
        //充值时长时间
        val chargeTime1: Long = DateUtil
          .caculateRequestTime(requestId, receiveNotifyTime)

        //充值成功标志 成功为 1  失败为 0
        val tag = if (bussinessRst.equals("0000") && chargeTime1 != 0L) 1 else 0
        //充值成功的充值花费时间
        val chargeTime: Long =
          if (bussinessRst.equals("0000"))
            DateUtil.caculateRequestTime(requestId, receiveNotifyTime)
          else 0
        //充值成功的充值金额
        val chargefee: BigDecimal = if (tag == 1) chargefee1 else 0L
        val day = receiveNotifyTime.substring(0, 8)
        val hour = receiveNotifyTime.substring(8, 10)
        //日期 小时 省份代码 充值结果  充值金额 充值时长(毫秒)）
        (day, hour, provinceCode, tag, chargefee, chargeTime)
      })


    //需求一：全网的充值订单量, 充值金额, 充值成功率，充值平均时长


    //充值订单量 充值金额 充值成功率 充值平均时长
      base_data
        .map(x => (1,x._4, x._5, x._6,1.0,0.0))
        .timeWindowAll(Time.seconds(10L))
      //充值订单量,充值成功数,充值金额,充值时长,充值成功率,平均充值时长
      .reduce((x,y)=>(x._1+1,x._2+y._2,x._3+y._3,x._4+y._4,(x._2+y._2)*1.0/(x._5+y._5),(x._4+y._4)*1.0/(x._2+y._2)))
      //充值订单量,充值金额,充值成功率,充值平均时长
      .map(x=>(x._1,x._3,(x._5*100).formatted("%.2f")+"%",(x._6/1000).formatted("%.2f")))
      .print()


    //需求二：实时统计全网的订单量数据
    val res2: DataStream[(String, String, Int, Int, Double)] = base_data.map(x => (x._1, x._2, x._4, 1, 1.0)) // 日期 小时 tag 1
      .keyBy(x => (x._1, x._2)) //按照 日期和小时分区
      //每5秒统计过去一小时结果
      .timeWindow(Time.hours(1), Time.seconds(5))
      // 日期 月份 充值总数 成功数 成功率
      .reduce((x, y) => (x._1, x._2, (x._4 + y._4).toInt, x._3 + y._3, ((x._3 + y._3) * 1.0 / (x._4 + y._4) * 100).formatted("%.2f").toDouble))

    env.execute()



  }


}




