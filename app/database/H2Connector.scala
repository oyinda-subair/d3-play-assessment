package database

import com.google.inject.AbstractModule
import com.typesafe.config.Config
import javax.inject.{Inject, Provider, Singleton}
import play.api.{Configuration, Environment}
import slick.jdbc.H2Profile.api._
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

@Singleton
class H2Connector @Inject()(config: Config) extends Provider[Database]{
  lazy val get = Database.forConfig("db", config)
}

class Module(environment: Environment, configuration: Configuration) extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[Database]).toProvider(classOf[H2Connector])
  }
}

//trait DbConfiguration extends DB {
//  lazy val config = DatabaseConfig.forConfig[JdbcProfile]("db")
//}
//
//trait DB{
//  val config: DatabaseConfig[JdbcProfile]
//  val db: JdbcProfile#Backend#Database = config.db
//}