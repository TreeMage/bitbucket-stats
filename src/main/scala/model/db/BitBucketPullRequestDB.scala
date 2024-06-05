package org.treemage
package model.db

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
