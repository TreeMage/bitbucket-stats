package org.treemage
package model.response.pullrequest

import zio.schema.*
import zio.schema.codec.{BinaryCodec, JsonCodec}

case class BitBucketPullRequestResponse(
    size: Int,
    page: Int,
    pagelen: Int,
    next: Option[String],
    previous: Option[String],
    values: List[PullRequestResponse]
)

object BitBucketPullRequestResponse:
  given Schema[BitBucketPullRequestResponse] =
    DeriveSchema.gen[BitBucketPullRequestResponse]
  given BinaryCodec[BitBucketPullRequestResponse] =
    JsonCodec.schemaBasedBinaryCodec
