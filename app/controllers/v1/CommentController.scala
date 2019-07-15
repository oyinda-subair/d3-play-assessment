package controllers.v1

import auth.AuthenticationAction
import controllers.form.CreateCommentForm
import javax.inject.Inject
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import repositories.CommentRepository

import scala.concurrent.ExecutionContext

class CommentController @Inject()(commentRepo: CommentRepository, authAction: AuthenticationAction, cc: ControllerComponents)
                                 (implicit ec: ExecutionContext) extends AbstractController(cc) with I18nSupport {

  val commentForm: Form[CreateCommentForm] = Form {
    mapping(
      "text" -> nonEmptyText
    )(CreateCommentForm.apply)(CreateCommentForm.unapply)
  }

  def createComment(postId: Int): Action[AnyContent] = authAction.async { implicit request =>
    val json = request.body.asJson.get

    val comment = json.as[CreateCommentForm]

    for {
      entity <- commentRepo.create(comment.text, postId, request.user.userId)
    } yield Ok(Json.toJson(entity))
  }

  def getCommentsByPostId(postId: Int): Action[AnyContent] = Action.async { implicit  request =>
    for {
      commentEntity <- commentRepo.getCommentsByPostId(postId)
    } yield Ok(Json.toJson(commentEntity))
  }
}
