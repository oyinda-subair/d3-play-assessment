package controllers

import database.UserRepository
import javax.inject._
import models.UserEntity
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.i18n._
import play.api.libs.json.Json
import play.api.mvc._
import views.html
import auth.Security._

import scala.concurrent.ExecutionContext

class UserController @Inject()(userRepo: UserRepository, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) with I18nSupport {

  val userForm: Form[CreateUserForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "email" -> nonEmptyText,
      "password" -> nonEmptyText,
      "confirmPassword" -> nonEmptyText
    )(CreateUserForm.apply)(CreateUserForm.unapply)
  }


  def getAllUser = Action.async { implicit request =>
    val users = userRepo.getAllUser
    users.map(u => Ok(Json.toJson(u)))
  }

  def saveUser = Action.async { implicit request =>
    val json = request.body.asJson.get
    val user = json.as[CreateUserForm]
    userRepo.getUserByEmail(user.email).map {
      case Some(_) => Conflict("user already exist with this email")
      case None => userRepo.create(user.name, user.email, hasPassword(user.password))
    }.map(_ => Ok)
  }
}
