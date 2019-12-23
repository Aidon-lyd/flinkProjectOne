import java.text.SimpleDateFormat

object DateUtil {

  def main(args: Array[String]): Unit = {
    println(caculateRequestTime("20170412030008471926341864858474", "20170412030043760"))
  }

  //获取充值时长
  def caculateRequestTime(start: String, endTime: String): Long = {

    if (start == null || "".equals(start) || endTime == null || "".equals(endTime)) {
      return 0
    }

    val dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS")
    val begin = dateFormat.parse(start.substring(0, 17)).getTime
    val end = dateFormat.parse(endTime).getTime

    end - begin

  }

}
