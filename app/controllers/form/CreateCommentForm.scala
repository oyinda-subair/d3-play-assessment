package controllers.form

import play.api.libs.json.{Format, Json}

case class CreateCommentForm(text: String)

object CreateCommentForm {
  implicit val format: Format[CreateCommentForm] = Json.format
}
