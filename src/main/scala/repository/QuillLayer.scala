package org.treemage
package repository

import io.getquill.*
import io.getquill.jdbczio.Quill
import org.postgresql.ds.PGSimpleDataSource
import zio.*
import org.treemage.config.ApplicationConfig

object QuillLayer:
  val live: ZLayer[ApplicationConfig, Nothing, Quill.Postgres[SnakeCase]] =
    ZLayer.fromZIO(
      for
        config <- ZIO.service[ApplicationConfig]
        dbConfig = config.postgres
      yield new Quill.Postgres(
        SnakeCase,
        new PGSimpleDataSource {
          setServerNames(Array(dbConfig.host))
          setPortNumbers(Array(dbConfig.port))
          setUser(dbConfig.username)
          setPassword(dbConfig.password)
          setDatabaseName(dbConfig.database)
        }
      )
    )
