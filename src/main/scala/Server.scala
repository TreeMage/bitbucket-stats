package org.treemage

import api.activity.{ActivityApiHandler, ActivityApiLive}
import api.pullrequest.{PullRequestApiHandler, PullRequestApiLive}
import api.users.{UserApiHandler, UserApiLive}
import config.ApplicationConfig
import repository.{
  BitBucketPullRequestActivityRepositoryLive,
  BitBucketPullRequestRepositoryLive,
  BitBucketUserRepositoryLive,
  QuillLayer
}
import service.*

import zio.*
import zio.config.typesafe.*
import zio.http.*

object ServerApp extends ZIOAppDefault:
  private val routes =
    UserApiHandler(UserApiLive).routes ++
      PullRequestApiHandler(PullRequestApiLive).routes ++
      ActivityApiHandler(ActivityApiLive).routes

  private val config = ZLayer
    .fromZIO(
      for
        configProvider <- ConfigProvider.fromHoconFilePathZIO(
          "config/application.json"
        )
        config <- configProvider.load(ApplicationConfig.config)
      yield config
    )
    .orDie

  def run: URIO[Any, ExitCode] = Server
    .serve(routes)
    .provide(
      Server.default,
      UserServiceLive.layer,
      PullRequestServiceLive.layer,
      ActivityServiceLive.layer,
      BitBucketUserRepositoryLive.layer,
      BitBucketPullRequestRepositoryLive.layer,
      BitBucketPullRequestActivityRepositoryLive.layer,
      QuillLayer.live,
      config.project(_.postgres)
    )
    .exitCode
