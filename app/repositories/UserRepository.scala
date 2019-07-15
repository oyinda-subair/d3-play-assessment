package repositories

import config.ApplicationConfig
import javax.inject.{Inject, Singleton}
import models.UserEntity
import play.api.db.slick.DatabaseConfigProvider
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class UserRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._
  import slick.migration.api._

  implicit val dialect: PostgresDialect = new PostgresDialect

  class UserTable(tag: Tag) extends Table[UserEntity](tag, "user_by_id"){
    def id: Rep[Int] = column[Int]("user_id", O.PrimaryKey, O.AutoInc)
    def name: Rep[String] = column[String]("name")
    val index1 = index("idx1", name)
    def email: Rep[String] = column[String]("email", O.Unique)
    val index2 = index("ids2", email)
    def password: Rep[String] = column[String]("password")

    def * = (id, name, email, password) <> ((UserEntity.apply _).tupled, UserEntity.unapply)
  }

  val Users = TableQuery[UserTable]

  def create(name: String, email: String, password: String): Future[UserEntity] = db.run {
    (Users.map(u ⇒ (u.name, u.email, u.password))
      returning Users.map(_.id)
      into ((idName, userId) ⇒ UserEntity(userId, idName._1, idName._2, idName._3))
      ) += (name, email, password)
  }

  def getAllUsers: Future[Seq[UserEntity]] =  db.run (Users.result)

  def getUserById(userId: Int): Future[Option[UserEntity]] = db.run {
    Users.filter(_.id === userId).result.headOption
  }

  def getUserByEmail(email: String): Future[Option[UserEntity]] = db.run {
    Users.filter(_.email === email).result.headOption
  }

//  def existsByEmail(email: String): Future[Boolean] =
//    db.run(Users.filter(_.email === email).exists.result)

  val init = TableMigration(Users).create.addColumns(_.id, _.name, _.email, _.password).addIndexes(_.index1, _.index2)
//  val seed = SqlMigration("insert into userById (name, password) values ('test name', 'password')")

  val migration = init

  if(ApplicationConfig.environment == "test") db.run(migration())
}
