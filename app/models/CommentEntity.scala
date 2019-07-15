package models

import play.api.libs.json.{Format, Json}

case class CommentEntity(commentId: Int, text: String, postId: Int, userId: Int)

object CommentEntity {
  implicit val format: Format[CommentEntity] = Json.format[CommentEntity]
}