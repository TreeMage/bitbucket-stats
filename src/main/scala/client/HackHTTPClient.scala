package org.treemage
package client

import zio.*
import zio.http.*
import zio.http.netty.NettyConfig

object HackHTTPClient:

  import java.net.{InetAddress, UnknownHostException}

  // TODO: Remove this as soon as I figured out why ipv6 is not working
  private final case class CustomSystemResolver() extends DnsResolver:
    override def resolve(
        host: String
    )(implicit
        trace: Trace
    ): ZIO[Any, UnknownHostException, Chunk[InetAddress]] =
      ZIO
        .attemptBlocking(InetAddress.getAllByName(host))
        // Hack to choose the first resolved address (which seems to be ipv4)
        .map(_.take(1))
        .refineToOrDie[UnknownHostException]
        .map(Chunk.fromArray)

  val default: ZLayer[Any, Throwable, Client] =
    (ZLayer.succeed(ZClient.Config.default) ++ ZLayer
      .succeed(
        NettyConfig.defaultWithFastShutdown
      ) ++ ZLayer.succeed(CustomSystemResolver())) >>> Client.live
