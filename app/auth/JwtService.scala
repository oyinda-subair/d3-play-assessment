package auth

import authentikat.jwt.{JsonWebToken, JwtClaimsSet, JwtHeader}
//import pdi.jwt.JwtClaim

import scala.util.{Failure, Success, Try}

class JwtService {

  val JwtSecretKey = "secretKey"
  val JwtSecretAlgo = "HS256"

  def createToken(payload: String): String = {
    val header = JwtHeader(JwtSecretAlgo)
    val claimsSet = JwtClaimsSet(payload)

    JsonWebToken(header, claimsSet, JwtSecretKey)
  }

  def isValidToken(jwtToken: String): Boolean =
    JsonWebToken.validate(jwtToken, JwtSecretKey)

  def isValidTokens(jwtToken: String) = {
    validateClaims(jwtToken) match {
      case Success(value) => decodePayload(value)
      case Failure(exception) => throw new Exception("The JWT did not pass validation")
    }
  }

  private val validateClaims = (claims: String) =>
    if (JsonWebToken.validate(claims, JwtSecretKey)) {
      Success(claims)
    } else {
      Failure(new Exception("The JWT did not pass validation"))
    }

  def decodePayload(jwtToken: String): Option[String] = {
    jwtToken match {
      case JsonWebToken(_, claimsSet: JwtClaimsSet, _) => Some(claimsSet.asJsonString)
      case _ => None
    }
  }
}
