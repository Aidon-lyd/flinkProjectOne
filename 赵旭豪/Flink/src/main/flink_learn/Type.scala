import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}

object Type {

  def main(args: Array[String]): Unit = {

    val env: StreamExecutionEnvironment =
      StreamExecutionEnvironment.getExecutionEnvironment

    //1.原生数据类型
    // java原生数据类型或者String类型
    import org.apache.flink.api.scala._
    val stream11: DataStream[Int] = env.fromElements(1, 2, 3, 4, 5)
    val stream1: DataStream[(String,Int)] = env.fromElements(("scala",2), ("flink",3))
    env.fromElements("scala", "spark", "flink")
    val stream2: DataStream[(String,Int)] = env.fromElements(("scala",1), ("spark",2), ("flink",6))
//    stream1.join(stream2).where().equalTo()


    //基本类型数组或String对象数组
    env.fromCollection(Array(1, 2, 3, 4, 5))
    env.fromCollection(List("scala", "flink"))
    //2.java Tuple上限25
    env.fromElements(new Tuple2("a", 1), new Tuple2("b", 2))
    //3.scala tuple
    env.fromElements(("a",1),("b",2))
    //4.POJOS类型 java,scala
    env.fromElements(new Person("a",1),new Person("b",2))
  }


}

class Person(var name:String,var age:Int){
  def this(){
    this(null,-1)
  }
}
