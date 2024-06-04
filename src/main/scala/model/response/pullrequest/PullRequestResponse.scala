package org.treemage
package model.response.pullrequest

import model.response.BitBucketUserResponse

import zio.schema.{DeriveSchema, Schema}

case class PullRequestResponse(
    id: Int,
    title: String,
    state: PullRequestState,
    created_on: String,
    author: BitBucketUserResponse
)

object PullRequestResponse:
  given Schema[PullRequestResponse] = DeriveSchema.gen[PullRequestResponse]
