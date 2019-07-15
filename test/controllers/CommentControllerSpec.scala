package controllers

import controllers.form.ApplicationToken
import models.{CommentEntity, PostEntity, UserEntity}
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.{FakeRequest, PlaySpecification, WithApplication}
import testkit.TestApplications
import testkit.Util.string10

import scala.concurrent.Future

class CommentControllerSpec extends PlaySpecification with TestApplications {
  var token: String = _
  var userId: Int = _

  "Comment Controller" should {
    "add comment to post" in new WithApplication {
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

      val userResult: Future[Result] = userController.saveUser(FakeRequest().withJsonBody(Json.parse(jsonBody)))
      val userEntity: UserEntity = contentAsJson(userResult).as[UserEntity]
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

      val postJsonBody: String =
        s"""
           {
           "title": "$string10",
           "content": "$string10"
           }
         """.stripMargin

      val postRequest:Future[Result] = route(app, FakeRequest(POST, s"/api/v1/posts").withHeaders(AUTHORIZATION -> s"Bearer $tokenString", "Content-type" -> "application/json").withBody(Json.parse(postJsonBody))).get
      val postEntity: PostEntity = contentAsJson(postRequest).as[PostEntity]
      postEntity.userId must equalTo(userEntity.userId)

      val postId = postEntity.postId
      val data = s"""{"text": "$string10" }"""
      val commentRequest:Future[Result] = route(app, FakeRequest(POST, s"/api/v1/post/$postId/comments").withHeaders(AUTHORIZATION -> s"Bearer $tokenString", "Content-type" -> "application/json").withBody(Json.parse(data))).get

      status(commentRequest) must equalTo(OK)
      val commentEntity = contentAsJson(commentRequest).as[CommentEntity]

      commentEntity.postId must equalTo(postId)
      commentEntity.userId must equalTo(userId)
    }
  }
}
