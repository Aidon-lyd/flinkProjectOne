<<<<<<< HEAD
package Recharge_Operate_Analysis.Utils

import java.sql.{Connection, DriverManager, PreparedStatement}

import Recharge_Operate_Analysis.Constant.Constant
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}


/**
  * Description:
  * Copyright (c),2019,JingxuanYan
  * This program is protected by copyright laws.
  *
  * @author 闫敬轩
  * @date 2019/12/18 19:54
  * @version 1.0
  */
object RechargeDistributionHelper extends RichSinkFunction[Tuple3[Int,Int,String]] with Serializable {
  var connection: Connection = _
  var ps: PreparedStatement = _
  var statement: java.sql.Statement = _
  val username = Constant.DB_USERNAME
  val password = Constant.DB_PASSWORD
  val url = Constant.DB_URL

  /**
    * 打开mysql的连接
    *
    * @param parameters
    */
  override def open(parameters: Configuration): Unit = {
    connection = DriverManager.getConnection(url, username, password)
    statement = connection.createStatement
    connection.setAutoCommit(false)
  }

  /**
    * 处理数据后写入mysql
    *
    * @param value
    */
   override def invoke(value: (Int, Int,String), context: SinkFunction.Context[_]): Unit = {
      val sql = "insert into rechargeDistribution(hour,chargefee,Cnt) values(?,?,?)"
          ps = connection.prepareStatement(sql)
          ps.setString(1,value._3)
          ps.setInt(2,value._1)
          ps.setInt(3, value._2)
          ps.execute()
          connection.commit()
  }

  /**
    * 关闭mysql的连接
    */
  override def close(): Unit = {
    if (ps != null) {
      ps.close()
    }
    if (connection != null) {
      connection.close()
    }
  }
}
=======
package Recharge_Operate_Analysis.Utils

import java.sql.{Connection, DriverManager, PreparedStatement}

import Recharge_Operate_Analysis.Constant.Constant
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}


/**
  * Description:
  * Copyright (c),2019,JingxuanYan
  * This program is protected by copyright laws.
  *
  * @author 闫敬轩
  * @date 2019/12/18 19:54
  * @version 1.0
  */
object RechargeDistributionHelper extends RichSinkFunction[Tuple3[Int,Int,String]] with Serializable {
  var connection: Connection = _
  var ps: PreparedStatement = _
  var statement: java.sql.Statement = _
  val username = Constant.DB_USERNAME
  val password = Constant.DB_PASSWORD
  val url = Constant.DB_URL

  /**
    * 打开mysql的连接
    *
    * @param parameters
    */
  override def open(parameters: Configuration): Unit = {
    connection = DriverManager.getConnection(url, username, password)
    statement = connection.createStatement
    connection.setAutoCommit(false)
  }

  /**
    * 处理数据后写入mysql
    *
    * @param value
    */
   override def invoke(value: (Int, Int,String), context: SinkFunction.Context[_]): Unit = {
      val sql = "insert into rechargeDistribution(hour,chargefee,Cnt) values(?,?,?)"
          ps = connection.prepareStatement(sql)
          ps.setString(1,value._3)
          ps.setInt(2,value._1)
          ps.setInt(3, value._2)
          ps.execute()
          connection.commit()
  }

  /**
    * 关闭mysql的连接
    */
  override def close(): Unit = {
    if (ps != null) {
      ps.close()
    }
    if (connection != null) {
      connection.close()
    }
  }
}
>>>>>>> 25ed57786f35ea5d8eab98caa78473da496da9b7
