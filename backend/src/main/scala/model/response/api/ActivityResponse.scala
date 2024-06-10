package org.treemage
package model.response.api

import model.db.ActivityType
import shared.model.domain.pullrequest
import shared.model.domain.pullrequest.PullRequestActivity

import zio.schema.*

import java.time.LocalDateTime
import java.util.UUID

case class ActivityResponse(
    pullRequestId: Int,
    // TODO: This should probably not refer to the DB type
    activityType: ActivityType,
    createdAt: LocalDateTime,
    authorId: UUID
)

object ActivityResponse:
  given Schema[ActivityResponse] = DeriveSchema.gen[ActivityResponse]
  def fromDomain(activity: PullRequestActivity): ActivityResponse =
    activity match
      case pullrequest.PullRequestActivity.Approval(
            pullRequestId,
            date,
            user
          ) =>
        ActivityResponse(pullRequestId, ActivityType.Approval, date, user.id)
      case pullrequest.PullRequestActivity.Comment(
            pullRequestId,
            id,
            createdOn,
            user
          ) =>
        ActivityResponse(
          pullRequestId,
          ActivityType.Comment,
          createdOn,
          user.id
        )
      case pullrequest.PullRequestActivity.Update(
            pullRequestId,
            date,
            author
          ) =>
        ActivityResponse(pullRequestId, ActivityType.Update, date, author.id)
