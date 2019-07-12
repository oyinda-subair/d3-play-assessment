package database

import java.util.UUID

import javax.inject.{Inject, Singleton}
import models.UserEntity
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
//import scala.concurrent.ExecutionContext.Implicits.global


@Singleton
class UserRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._
  import slick.migration.api._

//  val db = Database.forConfig("db")
  implicit val dialect: PostgresDialect = new PostgresDialect

  class UserTable(tag: Tag) extends Table[UserEntity](tag, "user_by_id"){
    def userId: Rep[Int] = column[Int]("user_id", O.PrimaryKey, O.AutoInc)
    def name: Rep[String] = column[String]("name")
    val index1 = index("idx1", name)
    def email: Rep[String] = column[String]("email", O.Unique)
    val index2 = index("ids2", email)
    def password: Rep[String] = column[String]("password")

    def * = (userId, name, email) <> ((UserEntity.apply _).tupled, UserEntity.unapply)
  }

  val user = TableQuery[UserTable]

  def create(name: String, email: String, password: String): Future[UserEntity] = db.run {
    (user.map(u ⇒ (u.name, u.email, u.password))
      returning user.map(_.userId)
      into ((idName, userId) ⇒ UserEntity(userId, idName._1, idName._2))
      ) += (name, email, password)
  }

  def getAllUser: Future[Seq[UserEntity]] =  db.run (user.result)

  def getUserById(userId: Int): Future[Option[UserEntity]] = db.run {
    user.filter(_.userId === userId).result.headOption
  }

  def getUserByEmail(email: String): Future[Option[UserEntity]] = db.run {
    user.filter(_.email === email).result.headOption
  }

  def existsByEmail(email: String): Future[Boolean] =
    db.run(user.filter(_.email === email).exists.result)

  val init = TableMigration(user).create.addColumns(_.userId, _.name, _.email, _.password).addIndexes(_.index1, _.index2)
//  val seed = SqlMigration("insert into userById (name, password) values ('test name', 'password')")

  val migration = init

  db.run(migration())
}
