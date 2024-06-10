package org.treemage
package model.db

import shared.model.domain.BitBucketUser
import shared.model.domain.pullrequest.{PullRequest, PullRequestState}

import java.time.LocalDateTime
import java.util.UUID

case class BitBucketPullRequestDB(
    id: Int,
    title: String,
    state: String,
    createdAt: LocalDateTime,
    updatedAt: LocalDateTime,
    authorId: UUID
)

object BitBucketPullRequestDB:
  def fromDomain(pr: PullRequest): BitBucketPullRequestDB =
    BitBucketPullRequestDB(
      pr.id,
      pr.title,
      pr.state.asString,
      pr.createdAt,
      pr.updatedAt,
      pr.author.id
    )

  extension (self: BitBucketPullRequestDB)
    def toDomain(author: BitBucketUser): Option[PullRequest] =
      for state <- PullRequestState.parse(self.state)
      yield PullRequest(
        self.id,
        self.title,
        state,
        self.createdAt,
        self.updatedAt,
        author
      )
