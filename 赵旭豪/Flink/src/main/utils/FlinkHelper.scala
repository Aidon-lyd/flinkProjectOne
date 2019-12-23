import org.apache.flink.streaming.api.{CheckpointingMode, TimeCharacteristic}
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

object FlinkHelper {


  def getEnv:StreamExecutionEnvironment={

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    //env.enableCheckpointing(1000)
    //env.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE)
    env
  }

}
