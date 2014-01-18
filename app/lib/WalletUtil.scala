package lib

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

import scala.collection.JavaConversions._
import models.WalletInfo

object WalletUtil{
  
  def walletToBytes(wallet:Wallet) : Array[Byte] = {
    val walletStream = new ByteArrayOutputStream()
    wallet.saveToFileStream(walletStream)
    walletStream.toByteArray()
  }
  
  def bytesToWallet(bytes:Array[Byte]) : Wallet = {
    Wallet.loadFromFileStream(new ByteArrayInputStream(bytes))
  }
  
}