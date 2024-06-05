package org.treemage
package model.domain.pullrequest

import model.db.BitBucketPullRequestDB
import model.domain.{BitBucketUser, parseBitBucketDateTime}
import org.treemage.model.response.bitbucket.pullrequest.{
  PullRequestResponse,
  PullRequestState
}

import java.time.LocalDateTime

case class PullRequest(
    id: Int,
    title: String,
    state: PullRequestState,
    createdAt: LocalDateTime,
    updatedAt: LocalDateTime,
    author: BitBucketUser
)

object PullRequest:
  def fromAPIResponse(response: PullRequestResponse): Option[PullRequest] =
    for
      createdAt <- parseBitBucketDateTime(response.created_on)
      updatedAt <- parseBitBucketDateTime(response.updated_on)
      author <- BitBucketUser.fromAPIResponse(response.author)
    yield PullRequest(
      response.id,
      response.title,
      response.state,
      createdAt,
      updatedAt,
      author
    )

  def fromDB(
      db: BitBucketPullRequestDB,
      author: BitBucketUser
  ): Option[PullRequest] =
    for state <- PullRequestState.parse(db.state)
    yield PullRequest(
      db.id,
      db.title,
      state,
      db.createdAt,
      db.updatedAt,
      author
    )

  extension (self: PullRequest)
    def toDB: BitBucketPullRequestDB =
      BitBucketPullRequestDB(
        self.id,
        self.title,
        self.state.asString,
        self.createdAt,
        self.updatedAt,
        self.author.id
      )
