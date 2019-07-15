package testkit

import controllers.v1.{CommentController, PostController, UserController}
import play.api.Application
import repositories.{CommentRepository, PostRepository, UserRepository}

trait TestApplications {

  def userController(implicit app: Application): UserController = {
    val app2Application = Application.instanceCache[UserController]
    app2Application(app)
  }

  def userRepository(implicit app: Application): UserRepository = {
    val app2UserRepository = Application.instanceCache[UserRepository]
    app2UserRepository(app)
  }

  def postController(implicit app: Application): PostController = {
    val app2Application = Application.instanceCache[PostController]
    app2Application(app)
  }

  def postRepository(implicit app: Application): PostRepository = {
    val app2UserRepository = Application.instanceCache[PostRepository]
    app2UserRepository(app)
  }

  def commentController(implicit app: Application): CommentController = {
    val app2Application = Application.instanceCache[CommentController]
    app2Application(app)
  }

  def commentRepository(implicit app: Application): CommentRepository = {
    val app2UserRepository = Application.instanceCache[CommentRepository]
    app2UserRepository(app)
  }

}
