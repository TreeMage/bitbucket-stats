package org.treemage
package repository

import io.getquill.*
import io.getquill.jdbczio.Quill
import org.treemage.model.db.{BitBucketActivityDB, BitBucketActivityDBForInsert}
import zio.{ZIO, ZLayer}

import java.sql.SQLException

case class BitBucketPullRequestActivityRepositoryLive(
    quill: Quill.Postgres[SnakeCase]
) extends BitBucketPullRequestActivityRepository:

  import quill.*
  inline given InsertMeta[BitBucketActivityDB] =
    insertMeta[BitBucketActivityDB](_.id)

  override def getById(
      id: Int
  ): ZIO[Any, SQLException, Option[BitBucketActivityDB]] =
    for result <- run(Schema.activities.filter(_.id == lift(id)))
    yield result.headOption

  override def create(
      pullRequestActivity: BitBucketActivityDBForInsert
  ): ZIO[Any, SQLException, Int] =
    val insert = BitBucketActivityDB(
      -1,
      pullRequestActivity.pullRequestId,
      pullRequestActivity.createdAt,
      pullRequestActivity.activityType,
      pullRequestActivity.authorId
    )
    for id <- run(
        Schema.activities
          .insertValue(lift(insert))
          .returningGenerated(_.id)
      ).orDieWith(_ => new SQLException(s"${insert}"))
    yield id

  override def deleteByPullRequestId(
      pullRequestId: Index
  ): ZIO[Any, SQLException, Unit] =
    for _ <- run(
        Schema.activities.filter(_.pullRequestId == lift(pullRequestId)).delete
      )
    yield ()

object BitBucketPullRequestActivityRepositoryLive:
  val layer: ZLayer[Quill.Postgres[
    SnakeCase
  ], Nothing, BitBucketPullRequestActivityRepositoryLive] =
    ZLayer.fromFunction(BitBucketPullRequestActivityRepositoryLive(_))
