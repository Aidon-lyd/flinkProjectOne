package cn.cafuc.cs.zzy.function

import cn.cafuc.cs.zzy.model.PayData
import org.apache.flink.api.common.functions.AggregateFunction

/**
  * (Long, Double, Long)
  * 充值订单量 充值金额  充值成功数
  */
class PayAggregate() extends AggregateFunction[PayData, (Long, Double, Long), (Long, Double, Long)] {
  //1. 初始化累加变量
  override def createAccumulator(): (Long, Double, Long) = (0L, 0.0, 0L)

  //2. 累加逻辑
  override def add(in: PayData, acc: (Long, Double, Long)): (Long, Double, Long) = {
    if (in.getBussinessRst != "0000") {
      (acc._1 + 1, acc._2, acc._3)
    } else {
      (acc._1 + 1, acc._2 + in.getChargefee, acc._3 + 1)
    }
  }

  //3. 获得最终结果
  override def getResult(acc: (Long, Double, Long)): (Long, Double, Long) = acc

  //4. 分区间聚合
  override def merge(acc: (Long, Double, Long), acc1: (Long, Double, Long)): (Long, Double, Long) = {
    (acc._1 + acc1._1, acc._2 + acc1._2, acc._3 + acc1._3)
  }
}
