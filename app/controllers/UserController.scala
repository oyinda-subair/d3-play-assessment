package controllers

import auth.{AuthAction, AuthenticationAction, JwtService, UserClaim}
import javax.inject._
import models.UserEntity
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.i18n._
import play.api.libs.json.Json
import play.api.mvc._
import auth.Security._
import repositories.UserRepository

import scala.concurrent.{ExecutionContext, Future}

class UserController @Inject()(userRepo: UserRepository, authAction: AuthenticationAction, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) with I18nSupport {

  val userForm: Form[CreateUserForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "email" -> nonEmptyText,
      "password" -> nonEmptyText
    )(CreateUserForm.apply)(CreateUserForm.unapply)
  }

  val loginForm: Form[LoginUserForm] = Form {
    mapping(
      "email" -> nonEmptyText,
      "password" -> nonEmptyText
    )(LoginUserForm.apply)(LoginUserForm.unapply)
  }

  def getAllUser: Action[AnyContent]  = Action.async { implicit request =>
    val users = userRepo.getAllUsers
    users.map(u => Ok(Json.toJson(u)))
  }

  def saveUser: Action[AnyContent]  = Action.async { implicit request =>
    val json = request.body.asJson.get

    val user = json.as[CreateUserForm]

    userRepo.getUserByEmail(user.email).flatMap {
      case Some(_) => Future.successful(Conflict("user exist with email address"))
      case None => userRepo.create(user.name, user.email, hashPassword(user.password)).map(entity => Ok(Json.toJson(entity)))
    }
  }

  def getUserById(id: Int): Action[AnyContent] = authAction.async { implicit request =>
//    request.user match {
//      case UserClaim(userId) if userId == id => userRepo.getUserById(id)
//      case UserClaim(userId) if userId != id => BadRequest("")
//    }
    userRepo.getUserById(id).map {
      case Some(entity) => Ok(Json.toJson(entity))
      case None => NotFound(s"user with id: $id was not found")
    }
  }

  def loginUser = Action.async { implicit request =>
    val json = request.body.asJson.get

    val user = json.as[LoginUserForm]

    userRepo.getUserByEmail(user.email).map {
      case Some(entity) if confirmPassword(user.password, entity.password) =>
        val payload = Json.toJson(UserClaim(entity.userId)).toString()
        val token = new JwtService().createToken(payload)
        Ok(Json.toJson(ApplicationToken(token)))
      case Some(entity) if !confirmPassword(user.password, entity.password) => BadRequest("Incorrect password")
      case None => NotFound(s"user with email: ${user.email} was not found")
    }
  }
}
