package org.treemage
package api.users

import service.UserService

import org.treemage.shared.api.users.{UserApiError, UserEndpoints}
import org.treemage.shared.model.api.UserResponse
import zio.*
import zio.http.codec.PathCodec.*
import zio.http.{Handler, Route, Routes}

import java.util.UUID

trait UserApi:
  def getById(id: UUID): ZIO[UserService, UserApiError, UserResponse]
  def getAll: ZIO[UserService, Nothing, List[UserResponse]]

case class UserApiHandler(api: UserApi):
  val routes: Routes[UserService, Nothing] = Routes.fromIterable(
    List(
      getById,
      getAll
    )
  )

  def getById: Route[UserService, Nothing] = UserEndpoints.getById.implement(
    Handler.fromFunctionZIO(api.getById)
  )

  def getAll: Route[UserService, Nothing] = UserEndpoints.getAll.implement(
    Handler.fromZIO(api.getAll)
  )
