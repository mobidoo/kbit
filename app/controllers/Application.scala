package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import com.google.bitcoin.core._
import com.google.bitcoin.store.BlockStore
import com.google.bitcoin.store.BlockStoreException
import com.google.bitcoin.store.MemoryBlockStore

import models.User
import security.Secured

object Application extends Controller with Secured {
  
  // sign-up form
  val signUpForm = Form(
    mapping(
      "name" -> nonEmptyText,
      "email" -> email, 
      "phone" -> nonEmptyText,
      "birth_day" -> date,
      "gender" -> nonEmptyText,
      "password" -> nonEmptyText    
    ) (User.apply)(User.unapply)
  )
  
  // login form
  val loginForm = Form {
    tuple(
      "Email" -> text, 
      "Password" -> text
    ) verifying("Invalid email or password", result => result match {
      case (email, password) => User.authenticate(email, password).isDefined
    })
  }
  
  def signUpAction = Action { implicit request =>
  	signUpForm.bindFromRequest.fold (
  	  formWithErrors => {
  	    println(formWithErrors)
	    BadRequest(views.html.join(formWithErrors, "test"))
	  },
	  userData => {
	    println("signup")
	    // create User 
	    User.create(userData.name, userData.email, userData.phoneNumber, userData.birthDay,
	        userData.gender, userData.password)
	        
	    // create wallet?        
	    Redirect(routes.Application.index)
	  }
	)    
  }
 
  def onUnauthorized(request: RequestHeader) = 
    Redirect(routes.Application.index)

  // index page
  def index = Action { request =>
    val email = request.session.get("email")
    Ok(views.html.index(loginForm, email))
  }
 
  def autheticate = Action { implicit request =>
  	println("auth")
  	loginForm.bindFromRequest.fold(
  	  formWithErrors => {
  	    println("error")
  	    BadRequest(views.html.index(formWithErrors))},
  	  user => Redirect(routes.Application.index).withSession("email" -> user._1) 
  	)
  }
  
  // logout
  def logout = Action{
    Redirect(routes.Application.index).withNewSession.flashing(
      "success" -> "You've been logged out"
    )
  }
  
  // sign up page
  def signUp = Action {
    Ok(views.html.join(signUpForm, "test"))
  }
  
  def isEmailExist(email:String) = Action{ implicit request =>
  	User.findByEmail(email).isDefined match {
  	  case true => Ok("false")
  	  case false => Ok("true")
  	} 
  }
  
  def javascriptRoutes = Action { implicit request =>
	Ok(
	  Routes.javascriptRouter("jsRoutes", None)(routes.javascript.Application.isEmailExist))
	  .as("text/javascript")
  } 
}

object MyHelpers {
  import views.html.helper.FieldConstructor
  implicit val myFields = FieldConstructor(views.html.myFieldConstructorTemplate.f)
}


