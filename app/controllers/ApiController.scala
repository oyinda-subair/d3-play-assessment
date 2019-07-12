package controllers

import auth.AuthAction
import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents}
import play.api.libs.json.Json
import repositories.DataRepository

@Singleton
class ApiController @Inject()(cc: ControllerComponents, dataRepository: DataRepository, authAction: AuthAction)
  extends AbstractController(cc) {

  // Create a simple 'ping' endpoint for now, so that we
  // can get up and running with a basic implementation
  def ping = Action { implicit request =>
    Ok("Hello, Scala!")
  }

  def getPost(postId: Int) = authAction { implicit request =>
    println("got here")
    dataRepository.getPost(postId) map { post =>

      Ok(Json.toJson(post))
    } getOrElse NotFound
  }

  def getComments(postId: Int) = authAction { implicit request =>

    Ok(Json.toJson(dataRepository.getComments(postId)))
  }

}