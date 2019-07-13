package repositories

import config.ApplicationConfig
import javax.inject.Inject
import models.PostEntity
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class PostRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val userRepo = new UserRepository(dbConfigProvider)

  import dbConfig._
  import profile.api._
  import slick.migration.api._
  import userRepo.Users

  implicit val dialect: PostgresDialect = new PostgresDialect

  class PostTable(tag: Tag) extends Table[PostEntity](tag, "post_by_id"){
    def postId: Rep[Int] = column[Int]("post_id", O.PrimaryKey, O.AutoInc)
    def title: Rep[String] = column[String]("title")
    val index1 = index("idx1", title)
    def content: Rep[String] = column[String]("content")
    def userId: Rep[Int] = column[Int]("user_id")

    def * = (postId, title, content, userId) <> ((PostEntity.apply _).tupled, PostEntity.unapply)

    def user = foreignKey("user_by_id_fk", userId, Users)(_.userId, onDelete = ForeignKeyAction.Cascade)

  }

  val post = TableQuery[PostTable]

  def create(title: String, content: String, userId: Int): Future[PostEntity] = db.run {
    (post.map(u ⇒ (u.title, u.content, u.userId))
      returning post.map(_.postId)
      into ((idPost, postId) ⇒ PostEntity(postId, idPost._1, idPost._2, idPost._3))
      ) += (title, content, userId)
  }

  def getAllPosts: Future[Seq[PostEntity]] =  db.run (post.result)

  object Posts extends TableQuery(new PostTable(_)) {
    val findById = this.findBy(_.postId)
    // more methods there
  }

  val init = TableMigration(post).create.addColumns(_.postId, _.title, _.content, _.userId).addIndexes(_.index1).addForeignKeys(_.user)
  //  val seed = SqlMigration("insert into userById (name, password) values ('test name', 'password')")

  val migration = init

  if(ApplicationConfig.environment == "test") db.run(migration())
}
