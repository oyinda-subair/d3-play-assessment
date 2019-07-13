package auth

import authentikat.jwt.{JsonWebToken, JwtClaimsSet, JwtHeader}
import play.libs.crypto._
import org.mindrot.jbcrypt.BCrypt

object Security {

  def hashPassword(password: String): String = {
    BCrypt.hashpw(password, BCrypt.gensalt())
  }

  def confirmPassword(password: String, hashPassword: String): Boolean = {
    BCrypt.checkpw(password, hashPassword)
  }
}
