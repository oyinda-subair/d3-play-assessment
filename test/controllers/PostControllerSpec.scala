package controllers

import controllers.form.ApplicationToken
import controllers.v1.PostController
import models.{PostEntity, UserEntity}
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll}
import play.api.Application
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.{FakeRequest, PlaySpecification, WithApplication}
import testkit.TestApplications
import testkit.Util.string10

import scala.concurrent.Future

class PostControllerSpec extends PlaySpecification with TestApplications {

  var token: String = _

  "Post Controller" should {

    "registered user should be able to create a post" in new WithApplication {

      val password: String = string10
      val email = s"$string10@email.com"
      val name = s"Post Bear-$string10"

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

      val userEntity: UserEntity = contentAsJson(result).as[UserEntity]

      val loginBody: String =
        s"""
           {
           "email": "$email",
           "password": "$password"
           }
         """.stripMargin

      val tokenResult: Future[Result] = userController.loginUser(FakeRequest().withJsonBody(Json.parse(loginBody)))

      val tokenString: String = contentAsJson(tokenResult).as[ApplicationToken].token

      val title: String = string10

      val postBody: String =
        s"""
           {
           "title": "$title",
           "content": "$string10"
           }
         """.stripMargin

      val request:Future[Result] = route(app, FakeRequest(POST, s"/api/v1/posts").withHeaders(AUTHORIZATION -> s"Bearer $tokenString", "Content-type" -> "application/json").withBody(Json.parse(postBody))).get

      status(result) must equalTo(OK)

      val postEntity: PostEntity = contentAsJson(request).as[PostEntity]

      postEntity.userId must equalTo(userEntity.userId)

    }
  }
}
