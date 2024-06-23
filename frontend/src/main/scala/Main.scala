package org.treemage

import util.{Success, Failure}

import ZioUtil.*
import api.{ApiExecutor, UsersApiClientLive}
import pages.users.UserList
import service.{UserServiceLive, UserService}

import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom
import org.treemage.shared.model.api.UserResponse
import zio.*
import zio.http.*

object Root:
  private val apiUrl = url"http://localhost:5000"
  private val userServiceLayer = (ZLayer.succeed(
    apiUrl
  ) ++ Client.default) >>> ApiExecutor.live >>> UsersApiClientLive.layer >>> UserServiceLive.layer
  lazy val runtime =
    userServiceLayer.toRuntime
      .provideSome(Scope.default)
      .unsafeRunToFuture

@main
def main(): Unit =
  lazy val container = dom.document.getElementById("app")
  Root.runtime.onComplete {
    case Success(runtime) =>
      ZIO
        .serviceWithZIO[UserService](_.getAll)
        .unsafeAsyncRunToEitherWithRuntime(runtime) {
          case Right(users) =>
            val userVar = Var(users)
            val userList = UserList(userVar)
            renderOnDomContentLoaded(container, userList.render)
          case Left(error) =>
            println(s"Error: $error")
        }
    case Failure(exception) => println(s"Error: $exception")
  }
  ()
