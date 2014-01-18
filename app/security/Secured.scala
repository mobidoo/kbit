package security

import play.api.mvc._
import scala.concurrent.Future

trait Secured {
  self: Controller =>
 
  /**
   * Retrieve the connected user id.
   */
  def username(request: RequestHeader) = request.session.get("email")
 
  /**
   * Redirect to login if the use in not authorized.
   */
  def onUnauthorized(request: RequestHeader): SimpleResult
  
  def isAuthenticated(f: => String => Request[AnyContent] => Result) =
    Security.Authenticated(username, onUnauthorized) { user =>
      Action(request => f(user)(request))
    }
  
  def isAuthenticatedAsync(f: => String => Request[AnyContent] => Future[SimpleResult]) =
    Security.Authenticated(username, onUnauthorized) { user =>
      Action.async(request => f(user)(request))
    }
}
