package org.treemage
package model.response.bitbucket

import model.response.bitbucket.PullRequestActivityResponseValueWrapper.Update
import model.response.bitbucket.{BitBucketUserResponse, parseBitBucketDateTime}
import shared.model.domain.BitBucketUser
import shared.model.domain.pullrequest.PullRequestActivity

import zio.schema.annotation.noDiscriminator
import zio.schema.codec.{BinaryCodec, JsonCodec}
import zio.schema.{DeriveSchema, Schema}

import java.time.LocalDateTime

case class PullRequestActivityResponse(
    pagelen: Int,
    next: Option[String],
    values: List[PullRequestActivityResponseValueWrapper]
)

case class PullRequestResponseInternal(`type`: String, id: Int, title: String)

object PullRequestActivityResponse:
  given Schema[PullRequestActivityResponse] =
    DeriveSchema.gen
  given BinaryCodec[PullRequestActivityResponse] =
    JsonCodec.schemaBasedBinaryCodec

@noDiscriminator enum PullRequestActivityResponseValueWrapper:
  case Approval(
      pull_request: PullRequestResponseInternal,
      approval: PullRequestActivityResponseValue.Approval
  )
  case Comment(
      pull_request: PullRequestResponseInternal,
      comment: PullRequestActivityResponseValue.Comment
  )
  case Update(
      pull_request: PullRequestResponseInternal,
      update: PullRequestActivityResponseValue.Update
  )

object PullRequestActivityResponseValueWrapper:

  private def parseUserFromAPIResponse(
      response: PullRequestActivityResponseValueWrapper
  ): Option[BitBucketUser] =
    val userResponse = response match
      case Approval(_, approval) => approval.user
      case Comment(_, comment)   => comment.user
      case Update(_, update)     => update.author
    userResponse.toDomain

  private def parseCreationDateFromAPIResponse(
      response: PullRequestActivityResponseValueWrapper
  ): Option[LocalDateTime] =
    val encoded = response match
      case Approval(_, approval) => approval.date
      case Comment(_, comment)   => comment.created_on
      case Update(_, update)     => update.date
    parseBitBucketDateTime(encoded)

  extension (self: PullRequestActivityResponseValueWrapper)
    def toDomain: Option[PullRequestActivity] =
      for
        author <- parseUserFromAPIResponse(self)
        date <- parseCreationDateFromAPIResponse(self)
      yield self match
        case PullRequestActivityResponseValueWrapper.Approval(
              pullRequest,
              approval
            ) =>
          PullRequestActivity.Approval(
            pullRequest.id,
            date,
            author
          )
        case PullRequestActivityResponseValueWrapper.Comment(
              pullRequest,
              comment
            ) =>
          PullRequestActivity.Comment(
            pullRequest.id,
            comment.id,
            date,
            author
          )
        case PullRequestActivityResponseValueWrapper.Update(
              pullRequest,
              update
            ) =>
          PullRequestActivity.Update(
            pullRequest.id,
            date,
            author
          )

enum PullRequestActivityResponseValue:
  case Approval(
      date: String,
      user: BitBucketUserResponse
  )
  case Comment(
      id: Int,
      created_on: String,
      user: BitBucketUserResponse
  )
  case Update(
      date: String,
      author: BitBucketUserResponse
  )
