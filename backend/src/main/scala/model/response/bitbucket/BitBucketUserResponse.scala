package org.treemage
package model.response.bitbucket

import shared.model.domain.BitBucketUser

import zio.schema.{DeriveSchema, Schema}

case class BitBucketUserResponse(
    display_name: String,
    uuid: String,
    account_id: String
)

object BitBucketUserResponse:
  given Schema[BitBucketUserResponse] = DeriveSchema.gen[BitBucketUserResponse]

  extension (self: BitBucketUserResponse)
    def toDomain: Option[BitBucketUser] =
      for id <- parseBitBucketUUID(self.uuid)
      yield BitBucketUser(
        id,
        self.display_name,
        self.account_id
      )
