package repositories

import config.ApplicationConfig
import javax.inject.Inject
import models.{CommentEntity, PostEntity}
import play.api.db.slick.DatabaseConfigProvider
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile


import scala.concurrent.{ExecutionContext, Future}

class PostRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val config = dbConfigProvider.get[JdbcProfile]
  import config._
  import profile.api._
  import slick.migration.api._


  val userRepo = new UserRepository(dbConfigProvider)
//  val commentRepo = new CommentRepository(dbConfigProvider)

  import userRepo.Users
//  import commentRepo.comments

  implicit val dialect: PostgresDialect = new PostgresDialect

  class PostTable(tag: Tag) extends Table[PostEntity](tag, "post_by_id"){
    def id: Rep[Int] = column[Int]("post_id", O.PrimaryKey, O.AutoInc)
    def title: Rep[String] = column[String]("title")
    val index1 = index("idx1", title)
    def content: Rep[String] = column[String]("content")
    def userId: Rep[Int] = column[Int]("user_id")

    def * = (id, title, content, userId) <> ((PostEntity.apply _).tupled, PostEntity.unapply)

    def users = foreignKey("user_by_id_fk", userId, Users)(_.id, onDelete = ForeignKeyAction.Cascade)
  }

  val Posts = TableQuery[PostTable]
  val postSchema = Posts.schema

  def create(title: String, content: String, userId: Int): Future[PostEntity] = db.run {
    (Posts.map(u ⇒ (u.title, u.content, u.userId))
      returning Posts.map(_.id)
      into ((idPost, postId) ⇒ PostEntity(postId, idPost._1, idPost._2, idPost._3))
      ) += (title, content, userId)
  }

  def getAllPosts: Future[Seq[PostEntity]] =  db.run (Posts.result)

  /*
  for {
        (ab, a) <- authorBooks join authors on (_.authorId === _.id)
      } yield (a, ab.bookId)
   */
  /*
  //    (for {
//      post <- Posts if post.postId === postId
//      comments <- Comments.filter(_.postId === postId)
//    } yield (post, comments)).result.headOption
   */
//  def getPostById(postId: Int) = db.run {
//    val innerJoin = for {
//      (post, comments) <- posts join comments on(_.id === _.postId)
//    } yield (post, comments)
//
//    innerJoin.filter(_._1.id === postId).sortBy(_._1.title).result.headOption
//  }


  def getPostsByUserId(userId: Int): Future[Seq[PostEntity]] = db.run {
    Posts.filter(_.userId === userId).result
  }

  /*
  def find(id: Int) = db.run(
  (for ((user, address) <- users join addresses if user.id === id)
    yield (user, address)).result.headOption)
   */

//  object Posts extends TableQuery(new PostTable(_)) {
//    val findById = this.findBy(_.id)
//    // more methods there
//  }

  val init = TableMigration(Posts).create.addColumns(_.id, _.title, _.content, _.userId).addIndexes(_.index1).addForeignKeys(_.users)
  //  val seed = SqlMigration("insert into userById (name, password) values ('test name', 'password')")

  val migration = init

  if(ApplicationConfig.environment == "test") db.run(migration())
}

