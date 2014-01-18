package lib

import play.api._
import com.google.bitcoin.params._

object BitcoinNetwork{
  val param = {
    val bitCoinNetwork = Play.current.configuration.getString("bitcoin.network").getOrElse("testnet")
    println(bitCoinNetwork)
    if (bitCoinNetwork == "testnet"){
      TestNet3Params.get()
    } else if (bitCoinNetwork == "regtest") {
      RegTestParams.get()
  	} else MainNetParams.get()
  }
}