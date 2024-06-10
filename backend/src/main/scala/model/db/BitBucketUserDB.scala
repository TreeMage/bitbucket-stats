package org.treemage
package model.db

import shared.model.domain.BitBucketUser

import zio.schema.{DeriveSchema, Schema}

import java.util.UUID

case class BitBucketUserDB(
    id: UUID,
    name: String,
    accountId: String
)

object BitBucketUserDB:
  given Schema[BitBucketUserDB] = DeriveSchema.gen[BitBucketUserDB]

  def fromDomain(user: BitBucketUser): BitBucketUserDB =
    BitBucketUserDB(
      user.id,
      user.name,
      user.accountId
    )

  extension (self: BitBucketUserDB)
    def toDomain: BitBucketUser =
      BitBucketUser(
        self.id,
        self.name,
        self.accountId
      )
