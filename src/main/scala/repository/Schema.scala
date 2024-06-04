package org.treemage
package repository

import io.getquill.*
import org.treemage.model.db.{
  BitBucketActivityDB,
  BitBucketActivityDBForInsert,
  BitBucketPullRequestDB,
  BitBucketUserDB
}

object Schema:
  inline def users: EntityQuery[BitBucketUserDB] =
    querySchema[BitBucketUserDB]("bit_bucket_users")
  inline def pullRequests: EntityQuery[BitBucketPullRequestDB] =
    querySchema[BitBucketPullRequestDB]("bit_bucket_pull_requests")
  inline def activities: EntityQuery[BitBucketActivityDB] =
    querySchema[BitBucketActivityDB]("bit_bucket_activities")
