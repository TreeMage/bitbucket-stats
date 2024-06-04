package org.treemage
package model.domain.pullrequest

import org.treemage.model.db.BitBucketPullRequestDB
import org.treemage.model.domain.{BitBucketUser, parseBitBucketDateTime}
import org.treemage.model.response.pullrequest.{
  PullRequestResponse,
  PullRequestState
}

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.util.Try

case class PullRequest(
    id: Int,
    title: String,
    state: PullRequestState,
    createdAt: LocalDateTime,
    author: BitBucketUser
)

object PullRequest:
  def fromAPIResponse(response: PullRequestResponse): Option[PullRequest] =
    for
      createdAt <- parseBitBucketDateTime(response.created_on)
      author <- BitBucketUser.fromAPIResponse(response.author)
    yield PullRequest(
      response.id,
      response.title,
      response.state,
      createdAt,
      author
    )

  extension (self: PullRequest)
    def toDB: BitBucketPullRequestDB =
      BitBucketPullRequestDB(
        self.id,
        self.title,
        self.state.asString,
        self.createdAt,
        self.author.id
      )
