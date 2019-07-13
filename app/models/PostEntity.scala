package models

import play.api.libs.json.{Format, Json}

case class PostEntity(postId: Int, title: String, content: String, userId: Int)

object PostEntity {
  implicit val format: Format[PostEntity] = Json.format[PostEntity]
}
