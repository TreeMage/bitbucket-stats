package org.treemage
package shared.model.domain.pullrequest

import shared.model.domain.BitBucketUser
import shared.model.domain.pullrequest.PullRequestState

import java.time.LocalDateTime

case class PullRequest(
    id: Int,
    title: String,
    state: PullRequestState,
    createdAt: LocalDateTime,
    updatedAt: LocalDateTime,
    author: BitBucketUser
)
