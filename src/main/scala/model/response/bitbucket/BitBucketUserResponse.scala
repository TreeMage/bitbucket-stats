package org.treemage
package model.response.bitbucket

import zio.schema.{DeriveSchema, Schema}

case class BitBucketUserResponse(
    display_name: String,
    uuid: String,
    account_id: String
)

object BitBucketUserResponse:
  given Schema[BitBucketUserResponse] = DeriveSchema.gen[BitBucketUserResponse]
