import org.apache.flink.api.scala.{DataSet, ExecutionEnvironment}

object WordCount_Batch {

  def main(args: Array[String]): Unit = {

    //获取上下文对象
    val  env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment

    //获取数据
    val text: DataSet[String] = env.readTextFile("src/data/words")

    import  org.apache.flink.api.scala._

    text.flatMap(_.split("\\s+"))
      .filter(_.nonEmpty)
      .map((_,1))
      .groupBy(0)
      .sum(1)
      .collect().foreach(println)


  }

}
