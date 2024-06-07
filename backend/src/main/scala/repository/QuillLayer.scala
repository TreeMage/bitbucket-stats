package org.treemage
package repository

import io.getquill.*
import io.getquill.jdbczio.Quill
import org.postgresql.ds.PGSimpleDataSource
import org.treemage.config.PostgresConfig
import zio.*

object QuillLayer:
  val live: ZLayer[PostgresConfig, Nothing, Quill.Postgres[SnakeCase]] =
    ZLayer.fromZIO(
      for config <- ZIO.service[PostgresConfig]
      yield new Quill.Postgres(
        SnakeCase,
        new PGSimpleDataSource {
          setServerNames(Array(config.host))
          setPortNumbers(Array(config.port))
          setUser(config.username)
          setPassword(config.password)
          setDatabaseName(config.database)
        }
      )
    )
