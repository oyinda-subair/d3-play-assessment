package auth

import play.api.mvc._
import authentikat.jwt._
import javax.inject.Inject
import play.api.http.HeaderNames
import play.api.libs.json.{Format, Json}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}


case class UserClaim(userId: Int)
object UserClaim {
  implicit val format: Format[UserClaim] = Json.format
}
case class UserRequests[A](user: UserClaim, token: String, request: Request[A]) extends WrappedRequest[A](request)

class AuthenticationAction @Inject()(bodyParser: BodyParsers.Default, jwtService: JwtService)(implicit ec: ExecutionContext) extends ActionBuilder[UserRequests, AnyContent] {

  override def parser: BodyParser[AnyContent] = bodyParser
  override protected def executionContext: ExecutionContext = ec

  private val headerTokenRegex = """Bearer (.+?)""".r

  override def invokeBlock[A](request: Request[A], block: UserRequests[A] => Future[Result]): Future[Result] = {
    extractBearerToken(request). map { token =>
      if (jwtService.isValidToken(token)) {
        jwtService.decodePayload(token).fold(
          Future.successful(Results.Unauthorized("Invalid credential"))
        ) { payload =>
          val userInfo = Json.parse(payload).validate[UserClaim].get
          block(UserRequests(userInfo, token, request))
        }
      }
      else {
        Future.successful(Results.Unauthorized("Invalid credential"))
      }
    } getOrElse Future.successful(Results.Unauthorized)

  }

  private def extractBearerToken[A](request: Request[A]): Option[String] =
    request.headers.get(HeaderNames.AUTHORIZATION) collect {
      case headerTokenRegex(token) => token
    }

}
