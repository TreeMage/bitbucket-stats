package org.treemage
package repository

import model.db.BitBucketUserDB

import io.getquill.*
import io.getquill.jdbczio.Quill
import zio.*

import java.sql.SQLException
import java.util.UUID

case class BitBucketUserRepositoryLive(quill: Quill.Postgres[SnakeCase])
    extends BitBucketUserRepository:
  import quill.*

  given MappedEncoding[UUID, String] = MappedEncoding(_.toString)
  given MappedEncoding[String, UUID] = MappedEncoding(UUID.fromString)

  override def getById(
      id: UUID
  ): ZIO[Any, SQLException, Option[BitBucketUserDB]] =
    for result <- run(Schema.users.filter(_.id == lift(id)))
    yield result.headOption

  override def createOrUpdate(
      user: BitBucketUserDB
  ): ZIO[Any, SQLException, UUID] =
    for r <- run(
        Schema.users
          .insertValue(lift(user))
          .onConflictUpdate(_.id)(
            (t, e) => t.name -> e.name,
            (t, e) => t.accountId -> e.accountId
          )
          .returning(_.id)
      )
    yield r

object BitBucketUserRepositoryLive:
  val layer
      : ZLayer[Quill.Postgres[SnakeCase], Nothing, BitBucketUserRepository] =
    ZLayer.fromFunction(BitBucketUserRepositoryLive(_))
