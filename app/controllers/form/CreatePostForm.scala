package controllers.form

import play.api.libs.json.{Format, Json}

case class CreatePostForm(title: String, content: String)

object CreatePostForm {
  implicit val format: Format[CreatePostForm] = Json.format
}
