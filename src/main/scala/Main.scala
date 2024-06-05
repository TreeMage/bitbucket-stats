package org.treemage

import org.treemage.client.{BitBucketClientLive, BitbucketClient}
import org.treemage.config.{ApplicationConfig, BitbucketConfig}
import org.treemage.model.RequestedCount
import org.treemage.model.response.pullrequest.{
  PullRequestActivityResponse,
  PullRequestState
}
import org.treemage.repository.{
  BitBucketPullRequestActivityRepositoryLive,
  BitBucketPullRequestRepositoryLive,
  BitBucketUserRepositoryLive,
  QuillLayer
}
import org.treemage.service.{
  ActivityServiceLive,
  CrawlingService,
  CrawlingServiceLive,
  PullRequestServiceLive,
  UserServiceLive
}
import zio.*
import zio.config.typesafe.*
import zio.http.*
import zio.http.netty.NettyConfig

import java.net.{InetAddress, UnknownHostException}

// TODO: Remove this as soon as I figured out why ipv6 is not working
private final case class CustomSystemResolver() extends DnsResolver {
  override def resolve(
      host: String
  )(implicit trace: Trace): ZIO[Any, UnknownHostException, Chunk[InetAddress]] =
    ZIO
      .attemptBlocking(InetAddress.getAllByName(host))
      // Hack to choose the first resolved address (which seems to be ipv4)
      .map(_.take(1))
      .refineToOrDie[UnknownHostException]
      .map(Chunk.fromArray)
}

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

  private val client = (ZLayer.succeed(ZClient.Config.default) ++ ZLayer
    .succeed(
      NettyConfig.defaultWithFastShutdown
    ) ++ ZLayer.succeed(CustomSystemResolver())) >>> Client.live

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
      config,
      BitBucketUserRepositoryLive.layer,
      BitBucketPullRequestRepositoryLive.layer,
      BitBucketPullRequestActivityRepositoryLive.layer,
      UserServiceLive.layer,
      PullRequestServiceLive.layer,
      ActivityServiceLive.layer,
      QuillLayer.live,
      BitBucketClientLive.layer,
      CrawlingServiceLive.layer,
      client,
      Scope.default
    )
}
