package org.treemage
package model.response.bitbucket.pullrequest

import org.treemage.model.response.bitbucket.BitBucketUserResponse
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
