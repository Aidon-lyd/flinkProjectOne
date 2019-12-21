package utils

object NumFormatUtil {
  def toInt(num:String):Int={
    try{
      num.toInt
    }catch {
      case _:Exception =>0
    }
  }

  def toDouble(num:String):Double={
    try{
      num.toDouble
    }catch {
      case _:Exception =>0d
    }
  }
}
