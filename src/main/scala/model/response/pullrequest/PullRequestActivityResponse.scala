package org.treemage
package model.response.pullrequest

import model.response.BitBucketUserResponse

import zio.schema.annotation.noDiscriminator
import zio.schema.{DeriveSchema, Schema}
import zio.schema.codec.{BinaryCodec, JsonCodec}

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
