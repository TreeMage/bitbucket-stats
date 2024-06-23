package org.treemage

import api.activity.{ActivityApiHandler, ActivityApiLive}
import api.crawl.{CrawlingApiHandler, CrawlingApiLive}
import api.pullrequest.{PullRequestApiHandler, PullRequestApiLive}
import api.users.{UserApiHandler, UserApiLive}
import client.{BitBucketClientLive, BitbucketClient, HackHTTPClient}
import config.ApplicationConfig
import repository.*
import service.*

import zio.*
import zio.config.typesafe.*
import zio.http.*

object ServerApp extends ZIOAppDefault:
  private val routes =
    UserApiHandler(UserApiLive).routes ++
      PullRequestApiHandler(PullRequestApiLive).routes ++
      ActivityApiHandler(ActivityApiLive).routes ++
      CrawlingApiHandler(CrawlingApiLive).routes

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

  private val server =
    ZLayer.succeed(Server.Config.default.port(5000)) >>> Server.live

  def run: ZIO[Any, Any, Any] = Server
    .serve(routes)
    .provide(
      Scope.default,
      server.orDie,
      HackHTTPClient.default.orDie,
      UserServiceLive.layer,
      PullRequestServiceLive.layer,
      ActivityServiceLive.layer,
      CrawlingServiceLive.layer,
      CrawlStateServiceLive.layer,
      BitBucketUserRepositoryLive.layer,
      BitBucketPullRequestRepositoryLive.layer,
      BitBucketPullRequestActivityRepositoryLive.layer,
      CrawlStateRepositoryLive.layer,
      BitBucketClientLive.layer,
      QuillLayer.live,
      config.project(_.postgres),
      config.project(_.bitbucket)
    )
    .debug
