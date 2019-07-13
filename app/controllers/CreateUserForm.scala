package controllers

import play.api.libs.json._

case class CreateUserForm(name: String, email: String, password: String)

object CreateUserForm {
  implicit val format: Format[CreateUserForm] = Json.format
}

case class LoginUserForm(email: String, password: String)

object LoginUserForm {
  implicit val format: Format[LoginUserForm] = Json.format
}

case class ApplicationToken(token: String)

object ApplicationToken {
  implicit val format: Format[ApplicationToken] = Json.format
}