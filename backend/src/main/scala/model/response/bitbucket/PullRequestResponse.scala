package org.treemage
package model.response.bitbucket

import model.response.bitbucket.BitBucketUserResponse
import shared.model.domain.pullrequest.{PullRequest, PullRequestState}

import zio.schema.{DeriveSchema, Schema}

case class PullRequestResponse(
    id: Int,
    title: String,
    state: PullRequestState,
    created_on: String,
    updated_on: String,
    author: BitBucketUserResponse
)

object PullRequestResponse:
  given Schema[PullRequestResponse] = DeriveSchema.gen[PullRequestResponse]

  extension (self: PullRequestResponse)
    def toDomain: Option[PullRequest] =
      for
        author <- self.author.toDomain
        createdAt <- parseBitBucketDateTime(self.created_on)
        updatedAt <- parseBitBucketDateTime(self.updated_on)
      yield PullRequest(
        self.id,
        self.title,
        self.state,
        createdAt,
        updatedAt,
        author
      )
