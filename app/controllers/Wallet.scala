package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import scala.concurrent.Future

import scala.collection.JavaConversions._

import java.net.InetAddress
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.math.BigInteger

import com.google.bitcoin.core._
import com.google.bitcoin.store.BlockStore
import com.google.bitcoin.store.BlockStoreException
import com.google.bitcoin.store.BoundedOverheadBlockStore
import com.google.bitcoin.store.MemoryBlockStore
import com.google.bitcoin.discovery._
import com.google.bitcoin.params._

import security.Secured

import reactivemongo.api._
import reactivemongo.bson._

import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection

import org.joda.time.DateTime
import models._
import lib._

object WalletController extends Controller with Secured with MongoController  {
      
  import models.JsonFormats._
  
  def walletColl: JSONCollection = 
    db.collection[JSONCollection]("wallet")

  private def findByEmail(email:String) : Future[Option[WalletInfo]] = 
    walletColl.find(Json.obj("email" -> email)).one[WalletInfo]
  
  private def insert(email:String, wallet:Wallet) : Unit = {
    val walletBytes = WalletUtil.walletToBytes(wallet)
    walletColl.insert(WalletInfo(email, walletBytes,Some(new DateTime()),None))
  }
  
  private def update(email:String, wallet:Wallet) : Unit = {
    walletColl.update(Json.obj("email"-> JsString(email)),
        WalletInfo(email, WalletUtil.walletToBytes(wallet), None, Some(new DateTime())))
  }
  
  def onUnauthorized(request: RequestHeader) = 
    Redirect(routes.Application.index)
   /* 
  def create(userId: String) = Action{ 
    val wallet = {    
      val _w = Wallets.get(userId)
      if (_w.isDefined) { 
        println("defined! userid")
        _w.get
      }
      else {
    	  val w = new Wallet(BitCoinNetwork.params)
    	  //w.addKey(new ECKey())
    	  Wallets.insert(userId, w)
    	  //w
      }
    }
    Ok(wallet.toString())
  }*/
  
  /**
  * deposit
  
  def deposit2 = 
    isAuthenticated{ email => request =>
      val wallet = 
        WalletInfo.findByEmail(email).getOrElse{
          // create new wallet
          val _wallet = new Wallet(BitcoinNetwork.param)
          _wallet.addKey(new ECKey())
          WalletInfo.insert(email, _wallet)
          _wallet
        }

      //generate ECKey
      val key : ECKey = {
        val keys = wallet.getKeys()
        if (keys.isEmpty()) {
          val _key = new ECKey()
          wallet.addKey(_key)
          // save the wallet
          WalletInfo.update(email, wallet)
          _key
        } else keys(0)
      }
      
      //.toAddress(BitcoinNetwork.param).toString
      val newAddr = key.toAddress(BitcoinNetwork.param).toString
      
      //schedule a task receiving the bitcoin.
      if (!BitcoinTask.isExistRevTask(email)) BitcoinTask.insertRevTask(email)
      
      Ok(views.html.bank3(email,newAddr))
  }*/
  
  def deposit = 
    isAuthenticatedAsync{ email => request =>
      // get the future of a wallet
      val futureWallet : Future[Option[WalletInfo]] =
        findByEmail(email)
      
      futureWallet.map {
        optionWallet =>
          optionWallet.map { walletInfo =>
            val wallet = WalletUtil.bytesToWallet(walletInfo.wallet)
 /*           } else {
              // TODO : move code | insert when a user is created
              val _wallet = new Wallet(BitcoinNetwork.param)
              _wallet.addKey(new ECKey())
          	  
              // save a wallet to mongo db
              insert(email, _wallet)
              _wallet
            }         
  */        
            val key : ECKey = {
              val keys = wallet.getKeys()
              if (keys.isEmpty()) {
              val _key = new ECKey()
                wallet.addKey(_key)

              // wallet updating.
                update(email, wallet) 
                _key
              } else keys(0)
            }

            val newAddr = key.toAddress(BitcoinNetwork.param)
          
          // notify the bitcoin actor to receive bitcoin
            TaskController.isExistTask(email, BitcoinRcv, "").map { optionTask =>
              if(!optionTask.isDefined)
        	    TaskController.putTask(email, BitcoinRcv,"")
            }
            
            Ok(views.html.bank3(email, newAddr.toString()))
          }.getOrElse(Ok(""))
        	  
      }
      
      //Ok(views.html.bank3(email,newAddr))
  }
  
  /**
   * balance
   */ 
  def balance = 
    isAuthenticatedAsync { email => request =>
      val futureWallet : Future[Option[WalletInfo]] = findByEmail(email)
      futureWallet.map {
        optionWallet : Option[WalletInfo] => 
          optionWallet.map {
            walletInfo  : WalletInfo => 
              val wallet = WalletUtil.bytesToWallet(walletInfo.wallet)
              //println(wallet.getBalance())
              //wallet.getTransactionsByTime().toList
              val balance = Utils.bitcoinValueToFriendlyString(wallet.getBalance())
              println(balance)
              Ok(views.html.trade1(email,balance))
          }.getOrElse(Ok("Err"))
          //result.getOrElse(Ok("Err"))
        }  
    }
  
  /*
  def send_offline(fromUserId:String, toUserId:String, coinStr:String) = Action{
	  val fromWalletOpt = WalletInfo.findByEmail(fromUserId) 
	  if (fromWalletOpt.isDefined){
	    val toWalletOpt = WalletInfo.findByEmail(toUserId)
	    if (toWalletOpt.isDefined) {
	      val fromWallet = fromWalletOpt.get
	      val toWallet   = toWalletOpt.get
	      val key  = new ECKey()
	      //val addr2 = toWallet.get
	      val addr = key.toAddress(BitcoinNetwork.param)
	      toWallet.addKey(key)
	      
	      val coin    = Utils.toNanoCoins(coinStr)
	      val sendReq = Wallet.SendRequest.to(addr, coin)
	      val tx      = fromWallet.sendCoinsOffline(sendReq)
	      if (tx == null) { 
	        Ok("no enough monery")
	      } else {
	        // Update Database
	        //Wallets.send(fromUserId,)
	        Ok(s"fromWallat : ${fromWallet.toString}\n toWallet:${toWallet.toString}")
	      }
	    } else 
	      Ok("err")
	  } else Ok("test")
  }
  
  def send(userId:String, address:String, coins:String) = Action {
    val walletObj = WalletInfo.findByEmail(userId)
    if (walletObj.isDefined){
      val wallet = walletObj.get
      val blockStore = new MemoryBlockStore(BitcoinNetwork.param)
      val blockChain = new BlockChain(BitcoinNetwork.param, wallet, blockStore)
      val peerGroup = new PeerGroup(BitcoinNetwork.param, blockChain)
      peerGroup.addPeerDiscovery(new DnsDiscovery(BitcoinNetwork.param))
      
      val value = Utils.toNanoCoins(coins)
      val addr = new Address(BitcoinNetwork.param, address)
      
      val sendReq = Wallet.SendRequest.to(addr,value)
      sendReq.feePerKb = Utils.toNanoCoins("0.0005")
      val tx = wallet.sendCoins(peerGroup, sendReq)
      	
      Ok((tx!=null).toString)
    } else Ok("No Wallet")
  }
  
  def refresh(userId:String) = Action {
    val walletObj = WalletInfo.findByEmail(userId)
    if (walletObj.isDefined) {
      val wallet = walletObj.get
      println(wallet.toString())
      val blockStore = new MemoryBlockStore(BitcoinNetwork.param)
      val blockChain = new BlockChain(BitcoinNetwork.param, wallet, blockStore)
      println("get chain")

      val peerGroup = new PeerGroup(BitcoinNetwork.param, blockChain)
      peerGroup.setUserAgent("kbitcoin", "0.1")
      
      peerGroup.addPeerDiscovery(new DnsDiscovery(BitcoinNetwork.param))
      peerGroup.addWallet(wallet)
      
      println("connect to peer")
      println("START DOWNLOADING BLOCKCHAIN")
      peerGroup.start()
      peerGroup.downloadBlockChain()
      peerGroup.stop()
      println("DONE;")
      println("disconnect to peer")
    	  
      WalletInfo.update(userId, wallet)
      println("update wallet")
      Ok(wallet.toString())
    } else {
      Ok("TEST")
    }
  }*/
  
  def view(userId:String) = Action{
    Ok("Test")
  }

}

/*
class BitcoinHelper(network:String){
  private val _network = 
    if (network == "testnet") TestNet3Params.get()
    else MainNetParams.get()
    
  private val _blockChainFile = s"$network.blockchain"
  private var _wallet : Wallet = _
  
  def loadWallet(userId:String){
    val walletObj = WalletInfo.findByEmail(userId)
    _wallet =
      if (walletObj.isDefined) walletObj.get
      else {
        val _wallet = new Wallet(_network)
	    _wallet.addKey(new ECKey())
	    WalletInfo.insert(userId, _wallet)
	    _wallet
      } 
  }
  
  def loadBlockChain = {
    
  }
  
}
*/
