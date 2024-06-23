package org.treemage
package api

import ZioUtil.*
import shared.api.users.{UserApiError, UserEndpoints}
import shared.model.api.UserResponse

import zio.*
import zio.http.endpoint.EndpointExecutor

import java.util.UUID

case class UsersApiClientLive(executor: EndpointExecutor[Unit])
    extends UsersApi:
  val getAll: ZIO[Any, Nothing, List[UserResponse]] =
    executor(UserEndpoints.getAll(())).withDefaultScope
  def getById(id: UUID): ZIO[Any, UserApiError, UserResponse] = executor(
    UserEndpoints.getById(id)
  ).withDefaultScope

object UsersApiClientLive:
  val layer: URLayer[EndpointExecutor[Unit], UsersApiClientLive] =
    ZLayer.fromFunction(UsersApiClientLive(_))
