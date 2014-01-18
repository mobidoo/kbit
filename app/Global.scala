package global

import play.api._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.concurrent.Akka
import akka.actor._

import com.google.bitcoin.core._
import com.google.bitcoin.core.Utils._
import com.google.bitcoin.kits.WalletAppKit
import com.google.bitcoin.discovery._

import java.io.{File, IOException}
import java.math.BigInteger

import scala.concurrent.duration.DurationInt
import models.WalletInfo

object Global extends GlobalSettings {
 // var bitcoinServer : ActorRef = _ 
  
  override def onStart(app: Application) {
    val kBitcoinActor  = Akka.system(app).actorOf(Props(new KBitcoinActor))
    //bitcoinServer ! GetBlockChain
   Akka.system(app).scheduler.schedule(0 seconds, 5 minutes, kBitcoinActor, "Start")
  }
}

/*
 * Message Types for Bitcoin Server
 */
case object GetBlockChain
case object GetKey
case class AddKey(key:ECKey)

class KBitcoinActor extends Actor with ActorLogging {
  import BitcoinServer._
  /*
  lazy val networkParams = getNetworkParams(networkId)

  val walletPrefix = filenameOption match {
    case Some(string) => string.replaceAll("\\.wallet$","")
    case None => networkId.toString.toLowerCase
  }
  
  val walletAppKit = (new WalletAppKit(networkParams, new File("."), walletPrefix) {
    override def onSetupCompleted() {
      log.debug(s"Bitcoin wallet has ${wallet.getKeychainSize} keys in its keychain")
      log.debug("Starting download of block chain")
      wallet addEventListener walletEventListener
    }
  })
   
  walletAppKit
  .setAutoSave(true)
  .startAndWait()
    
  def wallet = walletAppKit.wallet()
  def chain  = walletAppKit.chain()
  */
  /*
   * receive 
   */
  def receive = {
    case "Start" => 
      println("start")
    case _ => println("no message")
  }

}

/**
 * 
 */
object BitcoinServer{
  val log = org.slf4j.LoggerFactory.getLogger(this.getClass)
  
  val walletEventListener = new AbstractWalletEventListener {
    override def onCoinsSent(w: Wallet,
			     tx: Transaction,
			     prevBalance: BigInteger,
			     newBalance: BigInteger ) {
      super.onCoinsSent(w, tx, prevBalance, newBalance)
      log.info(s"onCoinsSent listener called: $prevBalance -> $newBalance\n$tx")
    }
    override def onCoinsReceived(
      wallet: Wallet,
      tx: Transaction,
      prevBalance: BigInteger,
      newBalance: BigInteger
    ) { synchronized {
      println(s"ALERT: you just received ${tx.getValueSentToMe(wallet)} microcents")
      println(s"  transaction ${tx.toString(null)}")
      println(s"  Previous balance: BTC ${bitcoinValueToFriendlyString(prevBalance)}")
      println(s"  New balance: BTC ${bitcoinValueToFriendlyString(newBalance)}")
    }}
  }
  
  private def getNetworkParams(networkId: String) = networkId match {
    case "testnet" => com.google.bitcoin.params.TestNet3Params.get()
    case "prodnet" => com.google.bitcoin.params.MainNetParams.get()
  }
}