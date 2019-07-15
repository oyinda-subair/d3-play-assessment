package controllers.v1

import auth.AuthenticationAction
import controllers.form.CreatePostForm
import javax.inject.Inject
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import play.api.libs.json._
import play.api.mvc._
import repositories.PostRepository

import scala.concurrent.ExecutionContext

class PostController @Inject()(postRepo: PostRepository, authAction: AuthenticationAction, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) with I18nSupport {
  val userForm: Form[CreatePostForm] = Form {
    mapping(
      "title" -> nonEmptyText,
      "content" -> nonEmptyText
    )(CreatePostForm.apply)(CreatePostForm.unapply)
  }


  def createPost: Action[AnyContent] = authAction.async { implicit request =>
    val json = request.body.asJson.get

    val post = json.as[CreatePostForm]

    for {
      entity <- postRepo.create(post.title, post.content, request.user.userId)
    } yield Ok(Json.toJson(entity))
  }

  def getPostByUserId(userId: Int): Action[AnyContent] = Action.async { implicit  request =>
    for {
      postEntity <- postRepo.getPostsByUserId(userId)
    } yield Ok(Json.toJson(postEntity))
  }

  def getAllPost = Action.async { implicit request =>
    for {
      postEntity <- postRepo.getAllPosts
    } yield Ok(Json.toJson(postEntity))
  }

}
