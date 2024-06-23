package org.treemage
package model.db

import shared.model.domain.BitBucketUser
import shared.model.domain.pullrequest.{ActivityType, PullRequestActivity}

import io.getquill.MappedEncoding

import java.time.LocalDateTime
import java.util.UUID

trait BitBucketActivityDBContent(
    pullRequestId: Int,
    createdAt: LocalDateTime,
    activityType: ActivityType,
    authorId: UUID
)

case class BitBucketActivityDBForInsert(
    pullRequestId: Int,
    createdAt: LocalDateTime,
    activityType: ActivityType,
    authorId: UUID
) extends BitBucketActivityDBContent(
      pullRequestId,
      createdAt,
      activityType,
      authorId
    )

case class BitBucketActivityDB(
    id: Int,
    pullRequestId: Int,
    createdAt: LocalDateTime,
    activityType: ActivityType,
    authorId: UUID
) extends BitBucketActivityDBContent(
      pullRequestId,
      createdAt,
      activityType,
      authorId
    )

object BitBucketActivityDB:
  def fromDomain(activity: PullRequestActivity): BitBucketActivityDBForInsert =
    activity match
      case PullRequestActivity.Approval(pullRequestId, date, user) =>
        BitBucketActivityDBForInsert(
          pullRequestId,
          date,
          ActivityType.Approval,
          user.id
        )
      case PullRequestActivity.Comment(pullRequestId, id, createdOn, user) =>
        BitBucketActivityDBForInsert(
          pullRequestId,
          createdOn,
          ActivityType.Comment,
          user.id
        )
      case PullRequestActivity.Update(pullRequestId, date, author) =>
        BitBucketActivityDBForInsert(
          pullRequestId,
          date,
          ActivityType.Update,
          author.id
        )
  extension (self: BitBucketActivityDB)
    def toDomain(
        author: BitBucketUser
    ): PullRequestActivity =
      self.activityType match
        case ActivityType.Approval =>
          PullRequestActivity.Approval(
            self.pullRequestId,
            self.createdAt,
            author
          )
        case ActivityType.Comment =>
          PullRequestActivity.Comment(
            self.pullRequestId,
            self.id,
            self.createdAt,
            author
          )
        case ActivityType.Update =>
          PullRequestActivity.Update(
            self.pullRequestId,
            self.createdAt,
            author
          )
