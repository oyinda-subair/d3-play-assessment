package models

import play.api.libs.json.{Format, Json}

case class Post(id: Int, content: String)

object Post {
  implicit val format: Format[Post] = Json.format[Post]
}
