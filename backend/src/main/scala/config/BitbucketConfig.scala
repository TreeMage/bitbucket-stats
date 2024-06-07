package org.treemage
package config

import zio.Config
import zio.config.*
import zio.config.magnolia.*

case class BitbucketConfig(
    workspace: String,
    repository: String,
    accessToken: String
)

object BitbucketConfig:
  val config: Config[BitbucketConfig] = deriveConfig[BitbucketConfig]
