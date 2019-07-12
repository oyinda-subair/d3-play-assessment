package models

import play.api.libs.json._

case class UserEntity(userId: Int, name: String, email: String)

object UserEntity {
  implicit  val format: Format[UserEntity] = Json.format
}
