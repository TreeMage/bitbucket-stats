package org.treemage
package repository

import model.db.BitBucketPullRequestDB

import io.getquill.*
import io.getquill.jdbczio.Quill
import zio.{ZIO, ZLayer}

import java.sql.SQLException

case class BitBucketPullRequestRepositoryLive(quill: Quill.Postgres[SnakeCase])
    extends BitBucketPullRequestRepository:
  import quill.*

  override def getById(
      id: Index
  ): ZIO[Any, SQLException, Option[BitBucketPullRequestDB]] =
    for result <- run(Schema.pullRequests.filter(_.id == lift(id)))
    yield result.headOption

  override def createOrUpdate(
      pullRequest: BitBucketPullRequestDB
  ): ZIO[Any, SQLException, Int] =
    for r <- run(
        Schema.pullRequests
          .insertValue(lift(pullRequest))
          .onConflictUpdate(_.id)(
            (t, e) => t.title -> e.title,
            (t, e) => t.state -> e.state,
            (t, e) => t.authorId -> e.authorId
          )
          .returning(_.id)
      )
    yield r

object BitBucketPullRequestRepositoryLive:
  val layer: ZLayer[Quill.Postgres[
    SnakeCase
  ], Nothing, BitBucketPullRequestRepository] =
    ZLayer.fromFunction(BitBucketPullRequestRepositoryLive(_))
