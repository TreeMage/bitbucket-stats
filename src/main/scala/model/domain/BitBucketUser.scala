package org.treemage
package model.domain

import model.db.BitBucketUserDB
import org.treemage.model.response.bitbucket.BitBucketUserResponse

import java.util.UUID

case class BitBucketUser(id: UUID, name: String, accountId: String)

object BitBucketUser:
  def fromAPIResponse(response: BitBucketUserResponse): Option[BitBucketUser] =
    for id <- parseBitBucketUUID(response.uuid)
    yield BitBucketUser(
      id,
      response.display_name,
      response.account_id
    )
  def fromDB(db: BitBucketUserDB): BitBucketUser =
    BitBucketUser(
      db.id,
      db.name,
      db.accountId
    )

  extension (self: BitBucketUser)
    def toDB: BitBucketUserDB =
      BitBucketUserDB(self.id, self.name, self.accountId)
