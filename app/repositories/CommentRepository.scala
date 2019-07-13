package repositories

import config.ApplicationConfig
import javax.inject.Inject
import models.CommentEntity
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

class CommentRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val userRepo = new UserRepository(dbConfigProvider)
  val postRepo = new PostRepository(dbConfigProvider)

  import dbConfig._
  import profile.api._
  import slick.migration.api._
  import userRepo.Users
  import postRepo.Posts

  implicit val dialect: PostgresDialect = new PostgresDialect

  class CommentTable(tag: Tag) extends Table[CommentEntity](tag, "comment_by_id"){
    def commentId: Rep[Int] = column[Int]("comment_id", O.PrimaryKey, O.AutoInc)
    def postId: Rep[Int] = column[Int]("post_id")
    def text: Rep[String] = column[String]("content")
    def userId: Rep[Int] = column[Int]("user_id")

    def * = (commentId, postId, text, userId) <> ((CommentEntity.apply _).tupled, CommentEntity.unapply)

    def users = foreignKey("user_by_id_fk", userId, Users)(_.userId, onDelete = ForeignKeyAction.Cascade)
    def posts = foreignKey("post_by_id_fk", postId, Posts)(_.postId, onDelete = ForeignKeyAction.Cascade)

  }

  val comment = TableQuery[CommentTable]

  val init = TableMigration(comment).create.addColumns(_.commentId, _.postId, _.text, _.userId).addForeignKeys(_.posts, _.users)
  //  val seed = SqlMigration("insert into userById (name, password) values ('test name', 'password')")

  val migration = init

  if(ApplicationConfig.environment == "test") db.run(migration())

}
