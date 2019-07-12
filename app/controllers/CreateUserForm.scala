package controllers

import play.api.libs.json._

case class CreateUserForm(name: String)

object CreateUserForm {
  implicit val format: Format[CreateUserForm] = Json.format
}
