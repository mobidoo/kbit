package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import com.google.bitcoin.core._
import com.google.bitcoin.store.BlockStore
import com.google.bitcoin.store.BlockStoreException
import com.google.bitcoin.store.MemoryBlockStore

import security.Secured

object BitcoinController extends Controller with Secured {
  def onUnauthorized(request: RequestHeader) = 
    Redirect(routes.Application.index)
  
}