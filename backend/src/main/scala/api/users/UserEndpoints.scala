package org.treemage
package api.users

import model.response.api.UserResponse
import service.UserService

import zio.*
import zio.http.*
import zio.http.endpoint.*
import zio.http.endpoint.EndpointMiddleware.None
import zio.schema.{DeriveSchema, Schema}

import java.util.UUID

object UserEndpoints:
  val getById: Endpoint[UUID, UUID, UserApiError, UserResponse, None] =
    Endpoint(Method.GET / "users" / uuid("id"))
      .out[UserResponse]
      .outError[UserApiError](Status.NotFound)

  val getAll: Endpoint[Unit, Unit, ZNothing, List[UserResponse], None] =
    Endpoint(Method.GET / "users")
      .out[List[UserResponse]]
