package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection

import models._
import models.JsonFormats._

import org.joda.time.DateTime

object TaskController extends Controller with MongoController  {
  def taskColl : JSONCollection =
    db.collection[JSONCollection]("task")

  // check if it is exists
  def isExistTask(email:String, taskType:TaskType, destAddr:String) = {
	taskColl.find(Json.obj("email" -> email, "taskType" -> taskType.toString,
	    "destAddr" -> destAddr)).one[Task]
  }

  // put a task
  def putTask(email:String, taskType:TaskType, destAddr:String) = {
    val created = new java.util.Date().getTime()
    val task = Task(email, taskType.toString(), destAddr, Some(new DateTime()), None, 0, false)
    taskColl.insert(task)
  }
  
}