package repositories

import config.ApplicationConfig
import javax.inject.Inject
import models.CommentEntity
import play.api.db.slick.DatabaseConfigProvider
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class CommentRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._
  import slick.migration.api._

  val userRepo = new UserRepository(dbConfigProvider)
  val postRepo = new PostRepository(dbConfigProvider)

  import userRepo.Users
  import postRepo.Posts

  implicit val dialect: PostgresDialect = new PostgresDialect

  class CommentTable(tag: Tag) extends Table[CommentEntity](tag, "comment_by_id"){
    def id: Rep[Int] = column[Int]("comment_id", O.PrimaryKey, O.AutoInc)
    def text: Rep[String] = column[String]("content")
    def postId: Rep[Int] = column[Int]("post_id")
    def userId: Rep[Int] = column[Int]("user_id")

    def * = (id, text, postId, userId) <> ((CommentEntity.apply _).tupled, CommentEntity.unapply)

    def users = foreignKey("user_by_id_fk", userId, Users)(_.id, onDelete = ForeignKeyAction.Cascade)
    def posts = foreignKey("post_by_id_fk", postId, Posts)(_.id, onDelete = ForeignKeyAction.Cascade)

  }

  val Comments = TableQuery[CommentTable]

  def create(text: String, postId: Int, userId: Int): Future[CommentEntity] = db.run {
    (Comments.map(c ⇒ (c.text, c.postId, c.userId))
      returning Comments.map(_.id)
      into ((idComment, commentId) ⇒ CommentEntity(commentId, idComment._1, idComment._2, idComment._3))
      ) += (text, postId, userId)
  }

  def getAllComments: Future[Seq[CommentEntity]] =  db.run (Comments.result)

//  def getPost = for {
//    post <- Posts
//    comments <- post.user
//  }

  def getCommentsByPostId(postId: Int): Future[Seq[CommentEntity]] = db.run {
    Comments.filter(_.postId === postId).result
  }

//  object Comment extends TableQuery(new CommentTable(_)) {
//    val findById = this.findBy(_.id)
//    // more methods there
//  }


  val init = TableMigration(Comments).create.addColumns(_.id, _.text, _.postId, _.userId).addForeignKeys(_.posts, _.users)
  //  val seed = SqlMigration("insert into userById (name, password) values ('test name', 'password')")

  val migration = init

  if(ApplicationConfig.environment == "test") db.run(migration())

}
