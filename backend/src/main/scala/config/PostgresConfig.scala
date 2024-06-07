package org.treemage
package config

import zio.Config
import zio.config.magnolia.*

case class PostgresConfig(
    host: String,
    port: Int,
    username: String,
    password: String,
    database: String
)

object PostgresConfig:
  val config: Config[PostgresConfig] = deriveConfig[PostgresConfig]
