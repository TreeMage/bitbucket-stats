package org.treemage
package model.response.api

import org.treemage.model.domain.pullrequest.PullRequest
import org.treemage.model.response.bitbucket.pullrequest.PullRequestState
import zio.schema.{DeriveSchema, Schema}

import java.time.LocalDateTime
import java.util.UUID

case class PullRequestResponse(
    id: Int,
    title: String,
    state: PullRequestState,
    createdAt: LocalDateTime,
    updatedAt: LocalDateTime,
    authorId: UUID
)

object PullRequestResponse:
  given Schema[PullRequestResponse] = DeriveSchema.gen[PullRequestResponse]

  def fromDomain(pr: PullRequest): PullRequestResponse =
    PullRequestResponse(
      id = pr.id,
      title = pr.title,
      state = pr.state,
      createdAt = pr.createdAt,
      updatedAt = pr.updatedAt,
      authorId = pr.author.id
    )
