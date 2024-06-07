package org.treemage
package config

import zio.Config
import zio.config.magnolia.*

case class ApplicationConfig(
    bitbucket: BitbucketConfig,
    postgres: PostgresConfig
)

object ApplicationConfig:
  val config: Config[ApplicationConfig] = deriveConfig[ApplicationConfig]
