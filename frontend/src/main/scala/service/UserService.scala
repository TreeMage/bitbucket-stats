package org.treemage
package service

import shared.api.users.UserApiError
import shared.model.api.UserResponse

import java.util.UUID
import zio.*

trait UserService:
  def getAll: ZIO[Any, Nothing, List[UserResponse]]
  def getById(id: UUID): ZIO[Any, UserApiError, UserResponse]
