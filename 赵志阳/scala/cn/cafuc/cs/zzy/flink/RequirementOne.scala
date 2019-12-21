package cn.cafuc.cs.zzy.flink

import java.util.{Calendar, Date}

import cn.cafuc.cs.zzy.constant.PayConstant
import cn.cafuc.cs.zzy.function.PayProcess
import cn.cafuc.cs.zzy.model.PayData
import cn.cafuc.cs.zzy.util.{FlinkUtil, PayJasonReader}
import org.apache.flink.api.common.functions.{AggregateFunction, FilterFunction, ReduceFunction}
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.streaming.connectors.redis.RedisSink
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig
import org.apache.flink.streaming.connectors.redis.common.mapper.{RedisCommand, RedisCommandDescription, RedisMapper}
import org.apache.flink.util.Collector

/**
  *
  * 统计全网的订单量、充值金额、充值成功数
  * 实时统计，每小时一个窗口，数据写入redis
  *
  * @Author : zzy
  * @Date : 2019/12/21
  * @Verson : 1.0
  */
object RequirementOne {


  def main(args: Array[String]): Unit = {
    //1. 获取流式执行环境和必要参数
    val env: StreamExecutionEnvironment = FlinkUtil.getEnviornment()
    val REDIS_HOST = PayConstant.REDIS_HOST
    val REDIS_PORT = PayConstant.REDIS_PORT
    //2. 获取数据通过自定义数据源
    val payDataStream: DataStream[(Long, Double, Long)] = env.addSource(new PayJasonReader("dir/in/cmcc.json"))
      .filter(new PayFilter())
      .keyBy(payData => payData.getServiceName.trim)
      .timeWindow(Time.hours(1))
      //      .aggregate(new PayAggregate())
      .process(new PayProcess())
    //3. 配置Redis环境
    val cnf: FlinkJedisPoolConfig = new FlinkJedisPoolConfig.Builder().setHost(REDIS_HOST).setPort(REDIS_PORT).build()
    payDataStream.addSink(new RedisSink[(Long, Double, Long)](cnf, new RedisPayMapper()))

    env.execute
  }

  class PayFilter() extends FilterFunction[PayData] {
    override def filter(t: PayData): Boolean = {
      if (t.getBussinessRst != "0000") {
        return false
      } else {
        return true
      }
    }
  }

  class RedisPayMapper() extends RedisMapper[(Long, Double, Long)] {
    override def getCommandDescription: RedisCommandDescription = {
      new RedisCommandDescription(RedisCommand.HSET, "pay_data")
    }

    override def getKeyFromData(t: (Long, Double, Long)): String = {
      // 因为以每个小时开滚动窗口，就以每个小时的聚合结果写入redis
      // 这里也能以年月日+小时为key   懒得凑了
      Calendar.getInstance().get(Calendar.HOUR).toString
    }

    override def getValueFromData(t: (Long, Double, Long)): String = {
      t.toString()
    }
  }

}









