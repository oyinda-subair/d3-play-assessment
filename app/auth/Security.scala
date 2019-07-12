package auth
import play.libs.crypto._
import org.mindrot.jbcrypt.BCrypt

object Security {
  def hasPassword(password: String): String = {
    BCrypt.hashpw(password, BCrypt.gensalt())
  }

  def confirmPassword(password: String, hashPassword: String): Boolean = {
    BCrypt.checkpw(password, hashPassword)
  }
}
