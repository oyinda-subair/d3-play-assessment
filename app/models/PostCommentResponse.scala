package models

import play.api.libs.json.{Format, Json}

case class CommentResponse(commentId: Int, text: String, userId: Int)
object CommentResponse {
  implicit val format: Format[CommentResponse] = Json.format
}

case class PostCommentResponse(postId: Int, title: String, comments: Seq[CommentResponse])

object PostCommentResponse {
  implicit val format: Format[PostCommentResponse] = Json.format
}
