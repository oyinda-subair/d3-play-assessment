package config

import com.typesafe.config.ConfigFactory

object ApplicationConfig {
  lazy val underlying = ConfigFactory.load()
  lazy val env = underlying.getString("ENVIRONMENT")
  lazy val environment = sys.env("DEV_ENVIRONMENT")
}
