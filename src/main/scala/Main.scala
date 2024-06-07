package org.treemage

import client.{BitBucketClientLive, BitbucketClient, HackHTTPClient}
import config.ApplicationConfig
import model.RequestedCount
import model.response.bitbucket.pullrequest.PullRequestState
import repository.*
import service.*

import zio.*
import zio.config.typesafe.*
import zio.http.*

object Main extends ZIOAppDefault {
  private val configPath = "config/application.json"
  // This seems to be the only fix for some IPv6 issues with netty at the moment.
  java.lang.System.setProperty("java.net.preferIPv4Stack", "true")

  private val config = ZLayer
    .fromZIO(
      for
        configProvider <- ConfigProvider.fromHoconFilePathZIO(configPath)
        config <- configProvider.load(ApplicationConfig.config)
      yield config
    )
    .orDie

  private val app =
    for
      total <- CrawlingService.fetchPullRequestActivityAndSave(
        PullRequestState.default,
        RequestedCount.Fixed(20)
      )
      _ <- Console.printLine(s"Total activities fetched: $total")
    yield ()

  def run: ZIO[ZIOAppArgs, Any, Any] =
    app.provide(
      config.project(_.bitbucket),
      config.project(_.postgres),
      BitBucketUserRepositoryLive.layer,
      BitBucketPullRequestRepositoryLive.layer,
      BitBucketPullRequestActivityRepositoryLive.layer,
      CrawlStateRepositoryLive.layer,
      UserServiceLive.layer,
      PullRequestServiceLive.layer,
      ActivityServiceLive.layer,
      CrawlStateServiceLive.layer,
      QuillLayer.live,
      BitBucketClientLive.layer,
      CrawlingServiceLive.layer,
      HackHTTPClient.default,
      Scope.default
    )
}
