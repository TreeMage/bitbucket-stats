package org.treemage
package api

import shared.api.users.UserApiError
import shared.model.api.UserResponse

import zio.*

import java.util.UUID

trait UsersApi:
  def getAll: ZIO[Any, Nothing, List[UserResponse]]
  def getById(id: UUID): ZIO[Any, UserApiError, UserResponse]
