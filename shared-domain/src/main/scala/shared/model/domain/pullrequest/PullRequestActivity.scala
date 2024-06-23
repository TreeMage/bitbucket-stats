package org.treemage
package shared.model.domain.pullrequest

import shared.model.domain.*

import java.time.LocalDateTime

enum PullRequestActivity:
  case Approval(pullRequestId: Int, date: LocalDateTime, user: BitBucketUser)
  case Comment(
      pullRequestId: Int,
      id: Int,
      createdOn: LocalDateTime,
      user: BitBucketUser
  )
  case Update(pullRequestId: Int, date: LocalDateTime, author: BitBucketUser)

object PullRequestActivity:
  extension (self: PullRequestActivity)
    def associatedUser: BitBucketUser =
      self match
        case Approval(_, _, user)   => user
        case Comment(_, _, _, user) => user
        case Update(_, _, author)   => author
