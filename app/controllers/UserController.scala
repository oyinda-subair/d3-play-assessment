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
import play.libs.crypto._

import scala.concurrent.ExecutionContext

class UserController @Inject()(userRepo: UserRepository, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) with I18nSupport {

  val userForm: Form[CreateUserForm] = Form {
    mapping(
      "name" -> nonEmptyText,
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
    for {
      _ <- userRepo.create(user.name, user.password)
    } yield Ok
  }
}
