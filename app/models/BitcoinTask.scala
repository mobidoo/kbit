package models

import play.api.db._
import play.api.Play._

import com.github.nscala_time.time.Imports._
import java.util.Date

import lib.AnormExtension._

case class BitcoinTask(email:String, taskType:String, destinationAddr:String, createTime:DateTime,
    updateTime:DateTime, retry:Int, done:Boolean)

object BitcoinTask{
  /*
  import anorm._
  import anorm.SqlParser._
  // find a user by email
  def getTasks : Stream[BitcoinTask] = DB.withConnection { implicit c =>
    SQL("SELECT email, task_type, dest_addr, retry, done FROM bitcoin_task WHERE complete='n'").apply().map { 
      row =>
      	BitcoinTask(row[String]("email"), row[String]("task_type"), row[String]("dest_addr"),
      	  row[Date]("created"), row[Date]("modified"), row[Int]("retry"), row[String]("done"))
    }    
  }
  
  def isExistRevTask(email:String) : Boolean = DB.withConnection { implicit c =>
    SQL("""SELECT * FROM bitcoin_task where email={email} and task_type='rcv' and done='n'""")
    .on("email" -> email).singleOpt().isDefined
  }
  
  def insertRevTask(email:String) = DB.withConnection { implicit c =>
    try {
  	  SQL("""INSERT INTO bitcoin_task(email, task_type, created, modified) VALUES({email}, 
  	    {taskType}, {created}, {modified})""").on("email" -> email, "taskType" -> "rcv",
  	    "created" -> DateTime.now, "modified" -> DateTime.now ).executeInsert()
    } catch {
      case e : Throwable => 
        println(e.toString)
        Unit
    }  
  }
  * */
 
}
