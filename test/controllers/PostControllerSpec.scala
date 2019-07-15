package controllers

import controllers.form.ApplicationToken
import models.{PostEntity, UserEntity}
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.{FakeRequest, PlaySpecification, WithApplication}
import testkit.TestApplications
import testkit.Util.string10

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class PostControllerSpec extends PlaySpecification with TestApplications {

  var token: String = _
  var userId: Int = _

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
      userId = userEntity.userId

      val loginBody: String =
        s"""
           {
           "email": "$email",
           "password": "$password"
           }
         """.stripMargin

      val tokenResult: Future[Result] = userController.loginUser(FakeRequest().withJsonBody(Json.parse(loginBody)))

      val tokenString: String = contentAsJson(tokenResult).as[ApplicationToken].token
      token = tokenString

      val title: String = string10

      val postBody: String =
        s"""
           {
           "title": "$title",
           "content": "$string10"
           }
         """.stripMargin

      val request:Future[Result] = route(app, FakeRequest(POST, s"/api/v1/posts").withHeaders(AUTHORIZATION -> s"Bearer $tokenString", "Content-type" -> "application/json").withBody(Json.parse(postBody))).get

      status(request) must equalTo(OK)

      val postEntity: PostEntity = contentAsJson(request).as[PostEntity]

      postEntity.userId must equalTo(userEntity.userId)

    }

    "get posts by userId" in new WithApplication {
      val request:Future[Result] = route(app, FakeRequest(GET, s"/api/v1/posts/user/$userId")
        .withHeaders(AUTHORIZATION -> s"Bearer $token", "Content-type" -> "application/json")).get

      status(request) must equalTo(OK)

      val postEntity: Seq[PostEntity] = contentAsJson(request).as[Seq[PostEntity]]

      postEntity.head.userId must equalTo(userId)
    }

    "get all posts" in new WithApplication {
      val id = Await.result(userRepository.create(string10, string10, string10), Duration.Inf).userId

      Await.result(postRepository.create(string10, string10, id), Duration.Inf)

      val request:Future[Result] = route(app, FakeRequest(GET, s"/api/v1/posts")
        .withHeaders(AUTHORIZATION -> s"Bearer $token", "Content-type" -> "application/json")).get

      status(request) must equalTo(OK)

      val postEntity: Seq[PostEntity] = contentAsJson(request).as[Seq[PostEntity]]

      postEntity.isEmpty must equalTo(false)
    }
  }
}
