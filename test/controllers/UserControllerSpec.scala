package controllers

import controllers.form.ApplicationToken
import controllers.v1.UserController
import models.UserEntity
import org.scalatestplus.play._
import play.api.Application
import play.api.libs.json.Json
import play.api.mvc._
import play.api.test.{FakeRequest, PlaySpecification, WithApplication}
import play.api.test.Helpers._
import repositories.UserRepository
import testkit.TestApplications
import testkit.Util._

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

class UserControllerSpec extends PlaySpecification with TestApplications {

  "UserController" should {

    "register new user" in new WithApplication {
      val password: String = string10
      val email = s"$string10@email.com"
      val name = s"FooBar $string10"

      val jsonBody: String =
        s"""
          {
          "name": "$name",
          "email": "$email",
           "password": "$password"
          }
        """.stripMargin

      val result: Future[Result] = userController.saveUser(FakeRequest().withJsonBody(Json.parse(jsonBody)))
      status(result) must equalTo(OK)

      val content: UserEntity = contentAsJson(result).as[UserEntity]

      val getUser:Option[UserEntity] = Await.result(userRepository.getUserById(content.userId), Duration.Inf)

      getUser.isDefined mustEqual true
      getUser.get.name mustEqual name
      getUser.get.email mustEqual email

     }

    "return conflict if email exist" in new WithApplication {
      val password: String = string10
      val email = s"$string10@email.com"
      val name = s"FooBar $string10"

      val jsonBody: String =
        s"""
          {
          "name": "$name",
          "email": "$email",
           "password": "$password"
          }
        """.stripMargin

      Await.result(userController.saveUser(FakeRequest().withJsonBody(Json.parse(jsonBody))), Duration.Inf)
      val result: Future[Result] = userController.saveUser(FakeRequest().withJsonBody(Json.parse(jsonBody)))

      status(result) must equalTo(CONFLICT)
    }

    "getUserById" in new WithApplication {
      val password: String = string10
      val email = s"$string10@email.com"
      val name = s"FooBar $string10"

      val jsonBody: String =
        s"""
          {
          "name": "$name",
          "email": "$email",
           "password": "$password"
          }
        """.stripMargin

      val result: Future[Result] = userController.saveUser(FakeRequest().withJsonBody(Json.parse(jsonBody)))
      val content: UserEntity = contentAsJson(result).as[UserEntity]

      val loginBody: String =
        s"""
           {
           "email": "$email",
           "password": "$password"
           }
         """.stripMargin

      val tokenResult: Future[Result] = userController.loginUser(FakeRequest().withJsonBody(Json.parse(loginBody)))

      val tokenString: String = contentAsJson(tokenResult).as[ApplicationToken].token

      val request: Future[Result] = route(app, FakeRequest("GET", s"/api/v1/user/${content.userId}").withHeaders(AUTHORIZATION -> s"Bearer $tokenString")).get

      status(request) must equalTo(OK)
      val content2: UserEntity = contentAsJson(request).as[UserEntity]
      content2.userId must equalTo(content.userId)

    }

    "login user" in new WithApplication {
      val password: String = string10
      val email = s"$string10@email.com"
      val name = s"FooBar $string10"

      val jsonBody: String =
        s"""
          {
          "name": "$name",
          "email": "$email",
           "password": "$password"
          }
        """.stripMargin

      Await.result(userController.saveUser(FakeRequest().withJsonBody(Json.parse(jsonBody))), Duration.Inf)

      val loginBody: String =
        s"""
           {
           "email": "$email",
           "password": "$password"
           }
         """.stripMargin

      val request: Future[Result] = route(app, FakeRequest(POST, "/login").withJsonBody(Json.parse(loginBody))).get

      status(request) must equalTo(OK)
      val content: ApplicationToken = contentAsJson(request).as[ApplicationToken]

      content.token.isEmpty must equalTo(false)
    }
  }
}
