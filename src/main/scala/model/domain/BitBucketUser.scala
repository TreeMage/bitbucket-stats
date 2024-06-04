package org.treemage
package model.domain

import model.response.BitBucketUserResponse

import org.treemage.model.db.BitBucketUserDB

import java.util.UUID

case class BitBucketUser(id: UUID, displayName: String, accountId: String)

object BitBucketUser:
  def fromAPIResponse(response: BitBucketUserResponse): Option[BitBucketUser] =
    for id <- parseBitBucketUUID(response.uuid)
    yield BitBucketUser(
      id,
      response.display_name,
      response.account_id
    )

  extension (self: BitBucketUser)
    def toDB: BitBucketUserDB =
      BitBucketUserDB(self.id, self.displayName, self.accountId)
