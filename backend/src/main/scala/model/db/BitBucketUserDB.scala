package org.treemage
package model.db

import zio.schema.{DeriveSchema, Schema}

import java.util.UUID

case class BitBucketUserDB(
    id: UUID,
    name: String,
    accountId: String
)

object BitBucketUserDB:
  given Schema[BitBucketUserDB] = DeriveSchema.gen[BitBucketUserDB]
