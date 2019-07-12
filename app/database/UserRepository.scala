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
    def password: Rep[String] = column[String]("password")

    def * = (userId, name) <> ((UserEntity.apply _).tupled, UserEntity.unapply)
  }

  val user = TableQuery[UserTable]

  def create(name: String, password: String): Future[UserEntity] = db.run {
    (user.map(u ⇒ (u.name, u.password))
      returning user.map(_.userId)
      into ((idName, userId) ⇒ UserEntity(userId, idName._1))
      ) += (name, password)
  }

  def getAllUser: Future[Seq[UserEntity]] =  db.run (user.result)

  def getUserById(userId: Int): Future[Option[UserEntity]] = db.run {
    user.filter(_.userId === userId).result.headOption
  }

  val init = TableMigration(user).create.addColumns(_.userId, _.name, _.password).addIndexes(_.index1)
//  val seed = SqlMigration("insert into userById (name, password) values ('test name', 'password')")

  val migration = init

  db.run(migration())
}
