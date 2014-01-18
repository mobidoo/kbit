package models
  
import play.api.libs.json.JsString 
import play.api.libs.json._
import play.api.data.validation.ValidationError
import org.apache.commons.codec.binary.Base64  
import org.joda.time.DateTime


/**
 * Wallet
 */
case class WalletInfo(
    email:String, 
    wallet:Array[Byte],
    created:Option[DateTime],
    modified:Option[DateTime])

/**
 * Task
 */
case class Task(
  email:String, 
  taskType:String, 
  destAddr:String, 
  created:Option[DateTime],
  modified:Option[DateTime], 
  retry:Int, 
  done:Boolean)

trait TaskType 
case object BitcoinRcv  extends TaskType{
  override def toString = "bitcoin_rcv"
}

case object BitcoinSend extends TaskType{
  override def toString = "bitcoin_send"
}
    
object WalletInfo{

}

object JsonFormats {

  implicit val byteArrayWrites = new Writes[Array[Byte]] {
    def writes(o: Array[Byte]) = JsString(new String((Base64.encodeBase64(o))))
  }
 
  implicit val byteArrayReads = new Reads[Array[Byte]] {
    def reads(json: JsValue) = json match {
      case JsString(value) => JsSuccess(Base64.decodeBase64(value.getBytes))
      case _ => JsError(Seq(JsPath() -> Seq(ValidationError("validate.error.expected.jsstring"))))
    }
  }
 
  // Generates Writes and Reads for Feed and User thanks to Json Macros
  implicit val walletFormat = Json.format[WalletInfo]
  implicit val taskFormat   = Json.format[Task]
}
