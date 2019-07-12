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

import scala.concurrent.{ExecutionContext, Future}

class UserController @Inject()(userRepo: UserRepository, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) with I18nSupport {

  val userForm: Form[CreateUserForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "email" -> nonEmptyText,
      "password" -> nonEmptyText
    )(CreateUserForm.apply)(CreateUserForm.unapply)
  }


  def getAllUser: Action[AnyContent]  = Action.async { implicit request =>
    val users = userRepo.getAllUser
    users.map(u => Ok(Json.toJson(u)))
  }

  def saveUser: Action[AnyContent]  = Action.async { implicit request =>
    val json = request.body.asJson.get
    println(s"json body: $json")
    val user = json.as[CreateUserForm]
//    userRepo.getUserByEmail(user.email).map {
//      case Some(_) => Conflict("user already exist with this email")
//      case None =>
//        val result = userRepo.create(user.name, user.email, hashPassword(user.password))
//        result.flatMap(entity => Ok(Json.toJson(entity)))
//    }

    for {
//      entityOpt <- userRepo.getUserByEmail(user.email)
      entity <- userRepo.create(user.name, user.email, hashPassword(user.password))
    } yield Ok(Json.toJson(entity))
  }

  def getUserById(id: Int): Action[AnyContent] = Action.async { implicit request =>
    userRepo.getUserById(id).map {
      case Some(entity) => Ok(Json.toJson(entity))
      case None => NotFound(s"user with id: $id was not found")
    }
  }
}
