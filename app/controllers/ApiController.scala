package controllers

import auth.AuthAction
import database.UserRepository
import javax.inject.{Inject, Singleton}
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, ControllerComponents}
import play.api.libs.json.Json
import repositories.DataRepository

import scala.concurrent.ExecutionContext


@Singleton
class ApiController @Inject()(cc: ControllerComponents, dataRepository: DataRepository, userRepo: UserRepository, authAction: AuthAction)(implicit ec: ExecutionContext)
  extends AbstractController(cc) with I18nSupport  {

//  val Home = Redirect(routes.UserController.list(0, 2, ""))


  // Create a simple 'ping' endpoint for now, so that we
  // can get up and running with a basic implementation
  def ping = Action { implicit request =>
    Ok("Hello, Scala!")
  }

  def getPost(postId: Int) = authAction { implicit request =>
    dataRepository.getPost(postId) map { post =>

      Ok(Json.toJson(post))
    } getOrElse NotFound
  }

  def getComments(postId: Int) = authAction { implicit request =>

    Ok(Json.toJson(dataRepository.getComments(postId)))
  }
}