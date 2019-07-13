package repositories

import javax.inject.Singleton
import models.{CommentEntity, PostEntity}

@Singleton
class DataRepository {

  // Specify a couple of posts for our API to serve up
  private val posts = Seq(
    PostEntity(1, "This is a blog post", "this is the content", 1),
    PostEntity(2, "Another blog post with awesome content", "this is the content", 2)
  )

  // Specify some comments for our API to serve up
  private val comments = Seq(
    CommentEntity(1, 1, "This is an awesome blog post", 1),
    CommentEntity(2, 1, "Thanks for the insights", 2),
    CommentEntity(3, 2, "Great, thanks for this post", 1)
  )

  /*
   * Returns a blog post that matches the specified id, or None if no
   * post was found (collectFirst returns None if the function is undefined for the
   * given post id)
   */
  def getPost(postId: Int): Option[PostEntity] = posts.collectFirst {
    case p if p.postId == postId => p
  }

  /*
   * Returns the comments for a blog post
   * If no comments exist for the specified post id, an empty sequence is returned
   * by virtue of the fact that we're using 'collect'
   */
  def getComments(postId: Int): Seq[CommentEntity] = comments.collect {
    case c if c.postId == postId => c
  }
}