package controllers

import database.UserRepository
import models.UserEntity
import org.scalatestplus.play._
import play.api.Application
import play.api.libs.json.Json
import play.api.mvc._
import play.api.test.{FakeRequest, PlaySpecification, WithApplication}
import testkit.Util._

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

class UserControllerSpec extends PlaySpecification {

  "UserController" should {

    def userController(implicit app: Application): UserController = {
      val app2Application = Application.instanceCache[controllers.UserController]
      app2Application(app)
    }

    def userRepository(implicit app: Application): UserRepository = {
      val app2UserRepository = Application.instanceCache[UserRepository]
      app2UserRepository(app)
    }

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
//      val request = FakeRequest(POST, "/api/users").withJsonBody(Json.parse(jsonBody))
//      val result = route(userController, request)

//        contentAsJson(result)
//
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
  }
}
