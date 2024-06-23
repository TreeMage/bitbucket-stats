package org.treemage
package api.users

import service.UserService

import org.treemage.shared.api.users.UserApiError
import org.treemage.shared.model.api.UserResponse
import zio.*

import java.util.UUID

case object UserApiLive extends UserApi:
  override def getById(
      id: UUID
  ): ZIO[UserService, UserApiError, UserResponse] =
    for
      userService <- ZIO.service[UserService]
      userMaybe <- userService.getById(id)
      user <- ZIO
        .fromOption(userMaybe)
        .orElseFail(UserApiError.NotFound(id))
    yield UserResponse.fromDomain(user)

  override def getAll: ZIO[UserService, Nothing, List[UserResponse]] =
    for
      userService <- ZIO.service[UserService]
      users <- userService.getAll
    yield users.map(UserResponse.fromDomain)
