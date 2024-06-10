package org.treemage
package model.response.api

import shared.model.domain.pullrequest.{PullRequest, PullRequestState}

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
      pr.id,
      pr.title,
      pr.state,
      pr.createdAt,
      pr.updatedAt,
      pr.author.id
    )
