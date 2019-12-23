import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.windowing.time.Time

object WordCount_Streaming {

  def main(args: Array[String]): Unit = {

    //获取上下文对象(初始化对象)
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    //获取数据
    val data=env.socketTextStream("hadoop111",6666)

    //引入计算时要使用的包
    import org.apache.flink.api.scala._

    //进行解析数据,并按照需求进行计算
    data.flatMap(_.split("\\s+"))
      .filter(_.nonEmpty)
      .map((_,1))
      .keyBy(0)
      .timeWindow(Time.seconds(10),Time.seconds(2))  //每隔两秒钟统计最近10秒的WordCount
//      .reduce((x,y)=>(x._1,x._2+y._2))
      .sum(1)
      .print()

    env.execute("Flink WordCount")


  }

}
