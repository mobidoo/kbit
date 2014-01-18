package lib

import java.security.MessageDigest
import scala.Array.canBuildFrom

object KBitcoinUtil{
  def md5Hash(text:String) : String = {
    MessageDigest.getInstance("MD5").digest(text.getBytes()).map(0xFF&_).map{
      "%02x".format(_)}.foldLeft(""){_+_}
  }
}


