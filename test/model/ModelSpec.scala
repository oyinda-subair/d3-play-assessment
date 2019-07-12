package model

import database.UserRepository
import org.scalatest.{Matchers, WordSpec}
import play.api.Application
import play.api.test.WithApplication
import testkit.Util._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class ModelSpec extends WordSpec with Matchers {

  "User model" should {
    def userRepository(implicit app: Application): UserRepository = {
      val app2UserRepository = Application.instanceCache[UserRepository]
      app2UserRepository(app)
    }

    "be retrieved by id" in new WithApplication {
      val userEntity = Await.result(userRepository.create("Dammy2", s"$string10@email.com", "pass"), Duration.Inf)

      val user = Await.result(userRepository.getUserById(userEntity.userId), Duration.Inf).get

      user.name shouldEqual userEntity.name
    }
  }
}
