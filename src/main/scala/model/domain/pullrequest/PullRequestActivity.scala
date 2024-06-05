package org.treemage
package model.domain.pullrequest

import model.db.{
  ActivityType,
  BitBucketActivityDB,
  BitBucketActivityDBForInsert
}
import model.domain.*
import model.domain.BitBucketUser
import org.treemage.model.response.bitbucket.pullrequest.PullRequestActivityResponseValueWrapper

import java.time.LocalDateTime

enum PullRequestActivity:
  case Approval(pullRequestId: Int, date: LocalDateTime, user: BitBucketUser)
  case Comment(
      pullRequestId: Int,
      id: Int,
      createdOn: LocalDateTime,
      user: BitBucketUser
  )
  case Update(pullRequestId: Int, date: LocalDateTime, author: BitBucketUser)

object PullRequestActivity:
  private def parseUserFromAPIResponse(
      response: PullRequestActivityResponseValueWrapper
  ): Option[BitBucketUser] =
    val user = response match
      case PullRequestActivityResponseValueWrapper.Approval(_, approval) =>
        approval.user
      case PullRequestActivityResponseValueWrapper.Comment(_, comment) =>
        comment.user
      case PullRequestActivityResponseValueWrapper.Update(_, update) =>
        update.author
    BitBucketUser.fromAPIResponse(user)

  private def parseCreationDateFromAPIResponse(
      response: PullRequestActivityResponseValueWrapper
  ): Option[LocalDateTime] =
    val encoded = response match
      case PullRequestActivityResponseValueWrapper.Approval(_, approval) =>
        approval.date
      case PullRequestActivityResponseValueWrapper.Comment(_, comment) =>
        comment.created_on
      case PullRequestActivityResponseValueWrapper.Update(_, update) =>
        update.date
    parseBitBucketDateTime(encoded)

  def fromAPIResponse(
      response: PullRequestActivityResponseValueWrapper
  ): Option[PullRequestActivity] =
    for
      author <- parseUserFromAPIResponse(response)
      date <- parseCreationDateFromAPIResponse(response)
    yield response match
      case PullRequestActivityResponseValueWrapper.Approval(
            pullRequest,
            approval
          ) =>
        Approval(
          pullRequest.id,
          date,
          author
        )
      case PullRequestActivityResponseValueWrapper.Comment(
            pullRequest,
            comment
          ) =>
        Comment(
          pullRequest.id,
          comment.id,
          date,
          author
        )
      case PullRequestActivityResponseValueWrapper.Update(
            pullRequest,
            update
          ) =>
        Update(
          pullRequest.id,
          date,
          author
        )

  def fromDB(
      db: BitBucketActivityDB,
      author: BitBucketUser
  ): PullRequestActivity =
    db.activityType match
      case ActivityType.Approval =>
        Approval(
          db.pullRequestId,
          db.createdAt,
          author
        )
      case ActivityType.Comment =>
        Comment(
          db.pullRequestId,
          db.id,
          db.createdAt,
          author
        )
      case ActivityType.Update =>
        Update(
          db.pullRequestId,
          db.createdAt,
          author
        )

  extension (self: PullRequestActivity)
    def toDB: BitBucketActivityDBForInsert =
      self match
        case Approval(pullRequestId, date, user) =>
          BitBucketActivityDBForInsert(
            pullRequestId,
            date,
            ActivityType.Approval,
            user.id
          )
        case Comment(pullRequestId, id, createdOn, user) =>
          BitBucketActivityDBForInsert(
            pullRequestId,
            createdOn,
            ActivityType.Comment,
            user.id
          )
        case Update(pullRequestId, date, author) =>
          BitBucketActivityDBForInsert(
            pullRequestId,
            date,
            ActivityType.Update,
            author.id
          )

    def associatedUser: BitBucketUser =
      self match
        case Approval(_, _, user)   => user
        case Comment(_, _, _, user) => user
        case Update(_, _, author)   => author
