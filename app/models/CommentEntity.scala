package models

import play.api.libs.json.{Format, Json}

case class CommentEntity(commentId: Int, postId: Int, text: String, userid: Int)

object CommentEntity {
  implicit val format: Format[CommentEntity] = Json.format[CommentEntity]
}