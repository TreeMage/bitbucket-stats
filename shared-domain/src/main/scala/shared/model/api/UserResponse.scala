package org.treemage
package shared.model.api

import shared.model.domain.BitBucketUser

import zio.schema.*

import java.util.UUID

case class UserResponse(id: UUID, name: String, accountId: String)

object UserResponse:
  given Schema[UserResponse] = DeriveSchema.gen[UserResponse]
  def fromDomain(user: BitBucketUser): UserResponse =
    UserResponse(user.id, user.name, user.accountId)
