package models

import play.api.db._
import play.api.Play.current

import java.util.Date

import lib.KBitcoinUtil._

case class User(name:String, email:String, phoneNumber:String, birthDay:Date, 
    gender:String, password:String)

object User{
  import anorm._
  import anorm.SqlParser._

  // find a user by email
  def findByEmail(email:String) : Option[User] = DB.withConnection { implicit c =>
    SQL("select name, email, phone_number, birth_day, gender, password from user where email={email}").on("email" -> email)
    .singleOpt().map { row =>
      User(row[String]("name"), row[String]("email"), row[String]("phone_number"),
          row[Date]("birth_day"), row[String]("gender"), row[String]("password"))
    }    
  }
  
  // insert a user
  def create(name:String, email:String, phoneNumber:String, birthDay:Date, gender:String, password:String) = DB.withConnection {
    implicit c =>
      SQL("""insert into user(name, email, phone_number, birth_day, gender, password) values 
          ({name},{email},{phoneNumber},{birthDay},{gender}, md5({password}))""")
      .on("name" -> name, "email" -> email, "phoneNumber" -> phoneNumber, "birthDay" -> birthDay, 
          "gender" -> gender, "password" -> password).executeInsert()
  }
  
  def authenticate(email:String, password:String) : Option[User] = {
    findByEmail(email).filter{
      user => md5Hash(password).equals(user.password)
    }
  }
  
}