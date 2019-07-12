package controllers

import database.UserRepository
import javax.inject._
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.i18n._
import play.api.mvc._

import scala.concurrent.ExecutionContext

class UserController @Inject()(userRepo: UserRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val userForm: Form[CreateUserForm] = Form {
    mapping(
      "name" -> nonEmptyText
    )(CreateUserForm.apply)(CreateUserForm.unapply)
  }

//  def index = Action { implicit request ⇒
//    Ok(views.html.index(userForm))
//  }
}
