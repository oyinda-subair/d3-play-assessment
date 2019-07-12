package models

import play.api.libs.json.{Format, Json}

case class Comment(id: Int, postId: Int, text: String, authorName: String)

object Comment {
  implicit val format: Format[Comment] = Json.format[Comment]
}