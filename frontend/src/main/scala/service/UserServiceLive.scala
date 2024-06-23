package org.treemage
package service

import ZioUtil.*
import api.UsersApi
import shared.api.users.UserApiError
import shared.model.api.UserResponse

import zio.*

import java.util.UUID

case class UserServiceLive(usersApi: UsersApi) extends UserService:
  def getAll: ZIO[Any, Nothing, List[UserResponse]] =
    usersApi.getAll
  def getById(id: UUID): ZIO[Any, UserApiError, UserResponse] =
    usersApi.getById(id)

object UserServiceLive:
  val layer: URLayer[UsersApi, UserServiceLive] =
    ZLayer.fromFunction(UserServiceLive(_))
